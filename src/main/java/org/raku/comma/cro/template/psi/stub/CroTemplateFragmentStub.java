package org.raku.comma.cro.template.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.comma.cro.template.psi.CroTemplateFragment;

public interface CroTemplateFragmentStub extends StubElement<CroTemplateFragment> {
    String getName();
}
