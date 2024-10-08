package org.raku.project.projectWizard;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StepSequence {
    private final List<ModuleWizardStep> myCommonSteps;
    private final List<Pair<ModuleWizardStep, Set<String>>> myCommonFinishingSteps = new ArrayList<>();
    private final MultiMap<String, ModuleWizardStep> mySpecificSteps = new MultiMap<>();
    private final MultiMap<String, ModuleWizardStep> mySpecificFinishingSteps = new MultiMap<>();
    @NonNls private final List<String> myTypes = new ArrayList<>();

    private List<Class<? extends ModuleWizardStep>> myIgnoredSteps = Collections.emptyList();
    private List<ModuleWizardStep> mySelectedSteps;

    public StepSequence(ModuleWizardStep... commonSteps) {
        myCommonSteps = new SmartList<>(commonSteps);
    }

    public void addCommonStep(@NotNull ModuleWizardStep step) {
        myCommonSteps.add(step);
    }

    public void addCommonFinishingStep(@NotNull ModuleWizardStep step, @Nullable Set<String> suitableTypes) {
        myCommonFinishingSteps.add(Pair.create(step, suitableTypes));
    }

    public List<ModuleWizardStep> getAllSteps() {
        final List<ModuleWizardStep> result = new ArrayList<>();
        result.addAll(myCommonSteps);
        result.addAll(mySpecificSteps.values());
        for (Pair<ModuleWizardStep, Set<String>> pair : myCommonFinishingSteps) {
            result.add(pair.getFirst());
        }
        result.addAll(mySpecificFinishingSteps.values());
        ContainerUtil.removeDuplicates(result);
        return result;
    }

    public void addStepsForBuilder(ModuleBuilder builder, WizardContext wizardContext, ModulesProvider modulesProvider) {
        String id = builder.getBuilderId();
        if (!mySpecificSteps.containsKey(id)) {
            mySpecificSteps.put(id, Arrays.asList(builder.createWizardSteps(wizardContext, modulesProvider)));
        }
        if (!mySpecificFinishingSteps.containsKey(id)) {
            mySpecificFinishingSteps.put(id, Arrays.asList(builder.createFinishingSteps(wizardContext, modulesProvider)));
        }
    }

    public void setTypes(Collection<String> types) {
        myTypes.clear();
        myTypes.addAll(types);
        mySelectedSteps = null;
    }

    public void setIgnoredSteps(@NotNull List<Class<? extends ModuleWizardStep>> steps) {
        myIgnoredSteps = steps;
    }

    public void setType(@Nullable @NonNls final String type) {
        setTypes(Collections.singletonList(type == null ? ModuleType.EMPTY.getId() : type));
    }


    @Nullable
    public ModuleWizardStep getNextStep(ModuleWizardStep step) {
        final List<ModuleWizardStep> steps = getSelectedSteps();
        final int i = steps.indexOf(step);
        return i < steps.size() - 1 ? steps.get(i + 1) : null;
    }

    @Nullable
    public ModuleWizardStep getPreviousStep(ModuleWizardStep step) {
        final List<ModuleWizardStep> steps = getSelectedSteps();
        final int i = steps.indexOf(step);
        return i > 0 ? steps.get(i - 1) : null;
    }

    public List<ModuleWizardStep> getSelectedSteps() {
        if (mySelectedSteps == null) {
            mySelectedSteps = new ArrayList<>();
            mySelectedSteps.addAll(myCommonSteps);
            for (String type : myTypes) {
                Collection<ModuleWizardStep> steps = mySpecificSteps.get(type);
                mySelectedSteps.addAll(steps);
            }
            for (Pair<ModuleWizardStep, Set<String>> pair : myCommonFinishingSteps) {
                Set<String> types = pair.getSecond();
                if (types == null || ContainerUtil.intersects(myTypes, types)) {
                    mySelectedSteps.add(pair.getFirst());
                }
            }
            for (String type : myTypes) {
                Collection<ModuleWizardStep> steps = mySpecificFinishingSteps.get(type);
                mySelectedSteps.addAll(steps);
            }
            ContainerUtil.removeDuplicates(mySelectedSteps);
        }

        return ContainerUtil.filter(mySelectedSteps, it -> !isIgnoredStep(it));
    }

    private boolean isIgnoredStep(ModuleWizardStep step) {
        return myIgnoredSteps.stream().anyMatch(it -> it.isInstance(step));
    }

    public void addSpecificStep(String type, ModuleWizardStep step) {
        mySpecificSteps.putValue(type, step);
    }
}
