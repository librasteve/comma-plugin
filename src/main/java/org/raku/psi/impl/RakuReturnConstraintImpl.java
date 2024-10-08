package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.raku.psi.RakuReturnConstraint;
import org.raku.psi.RakuTypeName;
import org.raku.psi.type.RakuType;
import org.raku.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;

import static org.raku.parsing.RakuElementTypes.TYPE_NAME;

public class RakuReturnConstraintImpl extends ASTWrapperPsiElement implements RakuReturnConstraint {
    public RakuReturnConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public RakuType getReturnType() {
        PsiElement typeName = findChildByType(TYPE_NAME);
        return typeName instanceof RakuTypeName
               ? ((RakuTypeName)typeName).inferType()
               : RakuUntyped.INSTANCE;
    }
}
