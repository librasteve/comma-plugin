package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6FatArrow;
import org.jetbrains.annotations.NotNull;

public class Perl6FatArrowImpl extends ASTWrapperPsiElement implements Perl6FatArrow {
    public Perl6FatArrowImpl(@NotNull ASTNode node) {
        super(node);
    }
}