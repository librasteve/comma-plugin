package edument.perl6idea.timeline;

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.runners.RunTab;
import com.intellij.execution.ui.*;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.notification.NotificationsManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.content.Content;
import edument.perl6idea.timeline.client.ClientEvent;
import edument.perl6idea.timeline.client.TimelineClient;
import edument.perl6idea.timeline.client.TimelineEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TimelineContentBuilder extends RunTab {
    private final static String TIMELINE_CONTENT_ID = "TimelineContent";
    private ExecutionResult myExecutionResult;

    public TimelineContentBuilder(@NotNull ExecutionResult executionResult,
                                  @NotNull ExecutionEnvironment environment) {
        super(environment.getProject(), GlobalSearchScope.allScope(environment.getProject()),
              environment.getRunner().getRunnerId(), "Timeline",
              environment.getRunProfile().getName());
        myEnvironment = environment;
        myExecutionResult = executionResult;
        myUi.getDefaults()
            .initTabDefaults(0, "Timeline", null)
            .initTabDefaults(1, "Console", null);
    }

    public RunContentDescriptor showRunContent(@Nullable RunContentDescriptor reuseContent,
                                               TimelineClient client) {
        RunContentDescriptor descriptor = createDescriptor(client);
        Disposer.register(descriptor, this);
        Disposer.register(myProject, descriptor);
        RunContentManagerImpl.copyContentAndBehavior(descriptor, reuseContent);
        myRunContentDescriptor = descriptor;
        return descriptor;
    }

    @NotNull
    private RunContentDescriptor createDescriptor(TimelineClient client) {
        final RunProfile profile = myEnvironment.getRunProfile();
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return new RunContentDescriptor(profile, myExecutionResult, myUi);
        }
        RunContentDescriptor contentDescriptor = new RunContentDescriptor(profile, myExecutionResult, myUi);
        addTimelineTab(client);
        addConsoleTab(profile, contentDescriptor);
        return contentDescriptor;
    }

    private void addTimelineTab(TimelineClient client) {
        TimelineView timeline = new TimelineView();
        Content content = myUi.createContent(TIMELINE_CONTENT_ID, timeline, "Timeline", null, null);
        content.setCloseable(false);
        myUi.addContent(content, 0, PlaceInGrid.center, false);

        client.connect(new TimelineEventListener() {
            @Override
            public void onEvent(ClientEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> timeline.updateWith(e));
            }

            @Override
            public void onError(Throwable e) {
                timeline.endLiveUpdates();
                String message = e.getMessage();
                if (message == null)
                    message = "unknown problem";
                Notifications.Bus.notify(
                        new Notification("Timeline connection error", null,
                                         "Timeline connection error", null,
                                         "Could not get timeline data: " + message,
                                         NotificationType.ERROR, null));
            }
        });
        myExecutionResult.getProcessHandler().addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                timeline.endLiveUpdates();
            }
        });
    }

    private void addConsoleTab(RunProfile profile, RunContentDescriptor contentDescriptor) {
        final ExecutionConsole console = myExecutionResult.getExecutionConsole();
        Content content = myUi.createContent(ExecutionConsole.CONSOLE_CONTENT_ID, console.getComponent(),
                                             "Console",
                                             AllIcons.Debugger.Console,
                                             console.getPreferredFocusableComponent());
        content.setCloseable(false);
        myUi.addContent(content, 1, PlaceInGrid.bottom, false);
        if (profile instanceof RunConfigurationBase) {
            if (console instanceof ObservableConsoleView && !ApplicationManager.getApplication().isUnitTestMode()) {
                ((ObservableConsoleView)console).addChangeListener(
                        new RunContentBuilder.ConsoleToFrontListener((RunConfigurationBase)profile,
                                myProject, myEnvironment.getExecutor(), contentDescriptor, myUi),
                        this);
            }
        }
    }
}