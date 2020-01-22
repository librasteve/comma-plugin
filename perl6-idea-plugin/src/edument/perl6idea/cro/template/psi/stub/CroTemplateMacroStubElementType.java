package edument.perl6idea.cro.template.psi.stub;

import com.intellij.psi.stubs.*;
import edument.perl6idea.cro.template.CroTemplateLanguage;
import edument.perl6idea.cro.template.psi.CroTemplateMacro;
import edument.perl6idea.cro.template.psi.impl.CroTemplateMacroImpl;
import edument.perl6idea.cro.template.psi.stub.impl.CroTemplateMacroStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CroTemplateMacroStubElementType extends IStubElementType<CroTemplateMacroStub, CroTemplateMacro> {
    public CroTemplateMacroStubElementType() {
        super("MACRO", CroTemplateLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "croTemplate.stub.macro";
    }

    @Override
    public CroTemplateMacro createPsi(@NotNull CroTemplateMacroStub stub) {
        return new CroTemplateMacroImpl(stub, this);
    }

    @NotNull
    @Override
    public CroTemplateMacroStub createStub(@NotNull CroTemplateMacro psi, StubElement parentStub) {
        return new CroTemplateMacroStubImpl(parentStub, psi.getName());
    }

    @Override
    public void serialize(@NotNull CroTemplateMacroStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeUTF(stub.getName());
    }

    @NotNull
    @Override
    public CroTemplateMacroStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new CroTemplateMacroStubImpl(parentStub, dataStream.readUTF());
    }

    @Override
    public void indexStub(@NotNull CroTemplateMacroStub stub, @NotNull IndexSink sink) {

    }
}
