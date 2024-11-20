package org.raku.comma.cro.template.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.TokenSet;
import org.raku.comma.cro.template.parsing.CroTemplateTokenTypes;
import org.raku.comma.cro.template.psi.CroTemplateFragmentCall;
import org.jetbrains.annotations.NotNull;

public class CroTemplateFragmentCallReference extends CroTemplateBaseReference<CroTemplateFragmentCall> {
    public CroTemplateFragmentCallReference(@NotNull CroTemplateFragmentCall element) {
        super(element, subNameTextRange(element));
    }

    private static TextRange subNameTextRange(CroTemplateFragmentCall element) {
        ASTNode[] subName = element.getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.SUB_NAME));
        if (subName.length == 0) {
            return new TextRange(0, element.getTextLength());
        }
        else {
            int startOffset = subName[0].getStartOffset() - element.getTextOffset();
            return new TextRange(startOffset, startOffset + subName[0].getTextLength());
        }
    }

    @Override
    protected CroTemplateSymbolKind getSymbolKind() {
        return CroTemplateSymbolKind.Sub;
    }
}
