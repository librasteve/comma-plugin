package org.raku.comma.cro.template.psi.stub.index;

import com.intellij.psi.stubs.StubIndexKey;
import org.raku.comma.cro.template.psi.CroTemplateFragment;
import org.raku.comma.cro.template.psi.CroTemplateMacro;
import org.raku.comma.cro.template.psi.CroTemplatePart;
import org.raku.comma.cro.template.psi.CroTemplateSub;

public class CroTemplateStubIndexKeys {
    public static final StubIndexKey<String, CroTemplateSub> TEMPLATE_SUB
            = StubIndexKey.createIndexKey("croTemplate.subs");
    public static final StubIndexKey<String, CroTemplateMacro> TEMPLATE_MACRO
            = StubIndexKey.createIndexKey("croTemplate.macros");
    public static final StubIndexKey<String, CroTemplateFragment> TEMPLATE_FRAGMENT
            = StubIndexKey.createIndexKey("croTemplate.macros");
    public static final StubIndexKey<String, CroTemplatePart> TEMPLATE_PART
        = StubIndexKey.createIndexKey("cro.template.parts");
}
