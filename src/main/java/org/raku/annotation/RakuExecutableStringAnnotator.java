package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.annotation.fix.UseExecutableDynamicVariableFix;
import org.raku.psi.RakuStrLiteral;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class RakuExecutableStringAnnotator implements Annotator {
    private static final Set<String> aliases = new HashSet<>();

    static {
        aliases.add("rakuidea");
        aliases.add("perl6");
        aliases.add("rakudo");
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RakuStrLiteral))
            return;

        String text = ((RakuStrLiteral)element).getStringText();
        if (aliases.contains(text))
            holder
                .newAnnotation(HighlightSeverity.WARNING, "If the Raku executable is meant, consider using the $*EXECUTABLE.absolute() call that supports many platforms (e.g. GNU/Linux, Windows, etc.)")
                .range(element)
                .withFix(new UseExecutableDynamicVariableFix(element)).create();
    }
}
