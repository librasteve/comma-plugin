package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.raku.psi.RakuIntLiteral;
import org.jetbrains.annotations.NotNull;

public class LeadingZeroAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        if (psiElement instanceof RakuIntLiteral) {
            String literal = psiElement.getText();
            if (literal.length() > 1 && literal.startsWith("0") && Character.isDigit(literal.charAt(1))) {
                holder.newAnnotation(HighlightSeverity.WARNING, "Leading 0 does not indicate octal in Raku; use 0o" + literal.substring(1))
                    .range(psiElement).create();
            }
        }
    }
}
