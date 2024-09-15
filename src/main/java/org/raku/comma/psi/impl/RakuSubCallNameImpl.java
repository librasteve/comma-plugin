package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.RakuSubCall;
import org.raku.comma.psi.RakuSubCallName;
import org.raku.comma.psi.RakuSubCallReference;
import org.jetbrains.annotations.NotNull;

public class RakuSubCallNameImpl extends ASTWrapperPsiElement implements RakuSubCallName {
    public RakuSubCallNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        PsiElement parent = getParent();
        boolean maybeCoercion = parent instanceof RakuSubCall && ((RakuSubCall)parent).maybeCoercion();
        return new RakuSubCallReference(this, maybeCoercion);
    }

    @NotNull
    @Override
    public String getCallName() {
        return getText();
    }
}