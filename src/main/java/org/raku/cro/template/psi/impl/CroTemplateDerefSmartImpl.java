package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.cro.template.psi.CroTemplateDerefSmart;
import org.jetbrains.annotations.NotNull;

public class CroTemplateDerefSmartImpl extends ASTWrapperPsiElement implements CroTemplateDerefSmart {
    public CroTemplateDerefSmartImpl(@NotNull ASTNode node) {
        super(node);
    }
}
