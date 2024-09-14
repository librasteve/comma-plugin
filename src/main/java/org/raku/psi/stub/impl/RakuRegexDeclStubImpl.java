package org.raku.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.parsing.RakuElementTypes;
import org.raku.psi.RakuRegexDecl;
import org.raku.psi.stub.RakuRegexDeclStub;
import org.raku.psi.stub.RakuScopedDeclStub;

public class RakuRegexDeclStubImpl extends StubBase<RakuRegexDecl> implements RakuRegexDeclStub {
    private final String regexName;
    private final boolean isExported;

    public RakuRegexDeclStubImpl(StubElement stub, String name, boolean exported) {
        super(stub, RakuElementTypes.REGEX_DECLARATION);
        this.regexName = name;
        isExported = exported;
    }

    @Override
    public String getRegexName() {
        return regexName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof RakuScopedDeclStub
               ? ((RakuScopedDeclStub)getParentStub()).getScope()
               : "has";
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
