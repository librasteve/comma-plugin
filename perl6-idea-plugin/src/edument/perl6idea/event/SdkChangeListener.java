package edument.perl6idea.event;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectExtension;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SdkChangeListener extends ProjectExtension {
    @Override
    public void projectSdkChanged(@Nullable Sdk sdk) {
        if (!ApplicationManager.getApplication().isUnitTestMode())
            Perl6SdkType.getInstance().invalidateCaches();
    }

    @Override
    public void readExternal(@NotNull Element element) {
    }

    @Override
    public void writeExternal(@NotNull Element element) {
    }
}
