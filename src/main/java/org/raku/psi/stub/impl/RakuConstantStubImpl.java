package org.raku.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.parsing.RakuElementTypes;
import org.raku.psi.RakuConstant;
import org.raku.psi.stub.RakuConstantStub;
import org.raku.psi.stub.RakuScopedDeclStub;
import org.jetbrains.annotations.Nullable;

public class RakuConstantStubImpl extends StubBase<RakuConstant> implements RakuConstantStub {
    @Nullable
    private final String constantName;
    private final boolean isExported;

    public RakuConstantStubImpl(StubElement stub, @Nullable String name, boolean isExported) {
        super(stub, RakuElementTypes.CONSTANT);
        this.constantName = name;
        this.isExported = isExported;
    }

    @Nullable
    @Override
    public String getConstantName() {
        return constantName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof RakuScopedDeclStub
               ? ((RakuScopedDeclStub)getParentStub()).getScope()
               : "our";
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
