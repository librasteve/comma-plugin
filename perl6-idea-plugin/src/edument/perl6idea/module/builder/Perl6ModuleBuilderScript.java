package edument.perl6idea.module.builder;

import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NotNull;

public class Perl6ModuleBuilderScript implements Perl6ModuleBuilderGeneric {
    @Override
    public void setupRootModel(@NotNull ModifiableRootModel model) {
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{""};
    }
}
