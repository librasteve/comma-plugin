package org.raku.pod;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ElementPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.raku.psi.PodBlockDelimited;
import org.raku.psi.PodBlockParagraph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.raku.parsing.RakuTokenTypes.POD_FINISH_TEXT;
import static org.raku.parsing.RakuTokenTypes.POD_TYPENAME;

@InternalIgnoreDependencyViolation
public class PodCompletionContributor extends CompletionContributor {
    public static final String[] POD_ABBREV = new String[]{
        "pod", "item", "table", "head1", "head2", "head3", "head4", "code", "para", "defn"
    };
    public static final String POD_FINISH = "finish";
    public static final String POD_PARAGRAPH = "for";
    public static final String POD_BEGIN = "begin";

    public PodCompletionContributor() {
        super();
        extend(CompletionType.BASIC, new ElementPattern<>() {
            @Override
            public boolean accepts(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean accepts(@Nullable Object o, ProcessingContext context) {
                if (o instanceof PsiElement) {
                    IElementType type = ((PsiElement)o).getNode().getElementType();
                    return type == POD_FINISH_TEXT || type == POD_TYPENAME;
                }
                return false;
            }

            @Override
            public ElementPatternCondition<PsiElement> getCondition() {
                return null;
            }
        }, new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                for (String keyword : POD_ABBREV) {
                    result.addElement(LookupElementBuilder.create(keyword));
                }
                PsiElement el = parameters.getPosition().getParent();
                if (!(el instanceof PodBlockParagraph || el instanceof PodBlockDelimited)) {
                    result.addElement(LookupElementBuilder.create(POD_PARAGRAPH));
                    result.addElement(LookupElementBuilder.create(POD_FINISH));
                    result.addElement(LookupElementBuilder.create(POD_BEGIN));
                    result.addElement(LookupElementBuilder.create("end"));
                }
            }
        });
    }
}
