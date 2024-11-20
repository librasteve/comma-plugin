package org.raku.comma.cro.template.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.cro.template.parsing.CroTemplateElementTypes;
import org.raku.comma.cro.template.psi.CroTemplateFragment;
import org.raku.comma.cro.template.psi.stub.CroTemplateFragmentStub;

public class CroTemplateFragmentStubImpl extends StubBase<CroTemplateFragment> implements CroTemplateFragmentStub {
    private final String name;

    public CroTemplateFragmentStubImpl(StubElement stub, String name) {
        super(stub, CroTemplateElementTypes.FRAGMENT);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
