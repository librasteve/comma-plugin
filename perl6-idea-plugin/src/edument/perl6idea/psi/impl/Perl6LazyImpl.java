package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Lazy;
import org.jetbrains.annotations.NotNull;

public class Perl6LazyImpl extends ASTWrapperPsiElement implements Perl6Lazy {
    public Perl6LazyImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        return "Seq";
    }
}
