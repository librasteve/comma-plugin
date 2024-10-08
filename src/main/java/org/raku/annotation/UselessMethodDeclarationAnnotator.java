package org.raku.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuPackageDecl;
import org.raku.psi.RakuRoutineDecl;
import org.jetbrains.annotations.NotNull;

public class UselessMethodDeclarationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof RakuRoutineDecl routine) {
            // Check that we've got a method.
            String kind = routine.getRoutineKind();
            if (!kind.equals("method") && !kind.equals("submethod"))
                return;

            // Check it is has-scoped (the default for methods) and has a name.
            if (!routine.getScope().equals("has"))
                return;
            PsiElement nameIdentifier = routine.getNameIdentifier();
            if (nameIdentifier == null)
                return;

            // Potentially useless; see if it's in a package.
            RakuPackageDecl packageDecl = PsiTreeUtil.getParentOfType(routine, RakuPackageDecl.class);
            boolean canHaveMethods = false;
            if (packageDecl != null) {
                String packageKind = packageDecl.getPackageKind();
                canHaveMethods = !(packageKind.equals("module") || packageKind.equals("package"));
            }
            if (!canHaveMethods)
                holder.newAnnotation(HighlightSeverity.WARNING, packageDecl == null
                        ? "Useless declaration of a method outside of any package"
                        : "Useless declaration of a method in a " + packageDecl.getPackageKind())
                    .range(nameIdentifier)
                    .create();
        }
    }
}
