package org.raku.comma.cro.template.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import org.raku.comma.cro.template.CroTemplateLanguage;
import org.raku.comma.cro.template.psi.CroTemplateFragment;
import org.raku.comma.cro.template.psi.impl.CroTemplateFragmentImpl;
import org.raku.comma.cro.template.psi.stub.impl.CroTemplateFragmentStubImpl;
import org.raku.comma.cro.template.psi.stub.index.CroTemplateStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CroTemplateFragmentStubElementType extends IStubElementType<CroTemplateFragmentStub, CroTemplateFragment> {
    public CroTemplateFragmentStubElementType() {
        super("FRAGMENT", CroTemplateLanguage.INSTANCE);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return ((CroTemplateFragment)node.getPsi()).getName() != null;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "croTemplate.stub.fragment";
    }

    @Override
    public CroTemplateFragment createPsi(@NotNull CroTemplateFragmentStub stub) {
        return new CroTemplateFragmentImpl(stub, this);
    }

    @NotNull
    @Override
    public CroTemplateFragmentStub createStub(@NotNull CroTemplateFragment psi, StubElement parentStub) {
        return new CroTemplateFragmentStubImpl(parentStub, psi.getName());
    }

    @Override
    public void serialize(@NotNull CroTemplateFragmentStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeUTF(stub.getName());
    }

    @NotNull
    @Override
    public CroTemplateFragmentStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new CroTemplateFragmentStubImpl(parentStub, dataStream.readUTF());
    }

    @Override
    public void indexStub(@NotNull CroTemplateFragmentStub stub, @NotNull IndexSink sink) {
        sink.occurrence(CroTemplateStubIndexKeys.TEMPLATE_FRAGMENT, stub.getName());
    }
}
