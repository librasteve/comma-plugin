package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.cro.template.psi.CroTemplateFragmentCall;
import org.raku.comma.cro.template.psi.CroTemplateElementFactory;
import org.raku.comma.cro.template.psi.CroTemplateFragment;
import org.raku.comma.cro.template.psi.CroTemplateSignature;
import org.raku.comma.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.raku.comma.cro.template.psi.reference.CroTemplateSymbolKind;
import org.raku.comma.cro.template.psi.stub.CroTemplateFragmentStub;
import org.jetbrains.annotations.NotNull;

import static org.raku.comma.cro.template.parsing.CroTemplateTokenTypes.FRAGMENT_NAME;

public class CroTemplateFragmentImpl extends StubBasedPsiElementBase<CroTemplateFragmentStub> implements CroTemplateFragment {
    public CroTemplateFragmentImpl(@NotNull ASTNode node) {
        super(node);
    }

    public CroTemplateFragmentImpl(final CroTemplateFragmentStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getNode().getElementType() + ")";
    }

    @Override
    public void offerAllTo(CroTemplateSymbolCollector collector) {
        CroTemplateSignature signature = PsiTreeUtil.getChildOfType(this, CroTemplateSignature.class);
        if (signature != null)
            signature.offerAllParametersTo(collector);
    }

    @Override
    public void declareToCollector(CroTemplateSymbolCollector collector) {
        collector.offer(getName(), CroTemplateSymbolKind.Fragment, this);
    }

    @Override
    public String getName() {
        CroTemplateFragmentStub stub = getStub();
        if (stub != null)
            return stub.getName();

        ASTNode[] macroName = getNode().getChildren(TokenSet.create(FRAGMENT_NAME));
        return macroName.length == 0 ? null : macroName[0].getText();
    }

    @Override
    public int getTextOffset() {
        ASTNode[] macroName = getNode().getChildren(TokenSet.create(FRAGMENT_NAME));
        return macroName.length == 0 ? super.getTextOffset() : macroName[0].getStartOffset();
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
}
