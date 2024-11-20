package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.cro.template.psi.CroTemplateArgList;
import org.raku.comma.cro.template.psi.CroTemplateFragmentCall;
import org.raku.comma.cro.template.psi.CroTemplateElementFactory;
import org.raku.comma.cro.template.psi.reference.CroTemplateFragmentCallReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.raku.comma.cro.template.parsing.CroTemplateTokenTypes.*;

public class CroTemplateFragmentCallImpl extends ASTWrapperPsiElement implements CroTemplateFragmentCall {
    public CroTemplateFragmentCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new CroTemplateFragmentCallReference(this);
    }

    @Override
    public String getName() {
        ASTNode[] subName = getNode().getChildren(TokenSet.create(FRAGMENT_NAME));
        return subName.length == 0 ? null : subName[0].getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        ASTNode oldNameNode = findChildByType(TokenSet.create(FRAGMENT_NAME));
        if (oldNameNode != null) {
            CroTemplateFragmentCall newName = CroTemplateElementFactory.createFragmentCall(getProject(), name);
            ASTNode newNameNode = newName.getNode().getChildren(TokenSet.create(FRAGMENT_NAME))[0];
            CodeEditUtil.replaceChild(getNode(), oldNameNode, newNameNode);
        }
        return this;
    }

    @Override
    public PsiElement[] getCallArguments() {
        CroTemplateArgList list = PsiTreeUtil.getChildOfType(this, CroTemplateArgList.class);
        if (list == null) return PsiElement.EMPTY_ARRAY;
        PsiElement node = list.getFirstChild();

        List<PsiElement> args = new ArrayList<>();

        while (node != null) {
            if (!(node instanceof PsiWhiteSpace ||
                  node.getNode().getElementType() == SYNTAX_WHITE_SPACE ||
                  node.getNode().getElementType() == OPEN_PAREN ||
                  node.getNode().getElementType() == CLOSE_PAREN ||
                  node.getNode().getElementType() == COMMA)) {
                args.add(node);
            }
            node = node.getNextSibling();
        }

        return args.toArray(PsiElement.EMPTY_ARRAY);
    }
}
