package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuStubCode;
import org.jetbrains.annotations.NotNull;

public class RakuStubCodeImpl extends ASTWrapperPsiElement implements RakuStubCode {
    public RakuStubCodeImpl(@NotNull ASTNode node) {
        super(node);
    }
}