package org.raku.pod;

import com.intellij.psi.PsiElement;
import org.raku.psi.RakuTrait;
import org.raku.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PodDomSubsetDeclarator extends PodDomDeclarator {
    private final String baseType;

    public PodDomSubsetDeclarator(int offset,
                                  @NotNull String shortName,
                                  @Nullable String globalName,
                                  List<PsiElement> docComments,
                                  RakuTrait exportTrait,
                                  String baseType) {
        super(offset, shortName, globalName, docComments, exportTrait);
        this.baseType = baseType;
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        // Render the name.
        builder.append("<h3 class=\"doc-package-name\">");
        builder.append(RakuUtils.escapeHTML(getPrimaryName()));
        builder.append(" <span class=\"doc-kind\">subset</span>");
        renderExportTags(builder, context);
        builder.append("</h3>");

        // Base type.
        builder.append("<table class=\"doc-prop-table\">\n");
        builder.append("<tr><td class=\"doc-prop-name\">Base Type</td><td><code>");
        builder.append(RakuUtils.escapeHTML(baseType == null ? "Any" : baseType));
        builder.append("</code></td></tr>");
        builder.append("</table>\n");

        // Add provided documentation.
        renderDocComments(builder, context);
    }
}
