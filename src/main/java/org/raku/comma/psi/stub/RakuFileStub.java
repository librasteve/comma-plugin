package org.raku.comma.psi.stub;

import com.intellij.psi.stubs.PsiFileStub;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.RakuPsiDeclaration;

import java.util.List;

public interface RakuFileStub extends PsiFileStub<RakuFile> {
    /* The name, inferred from path, that a `use` statement would be followed by
     * to resolve to this module. */
    String getCompilationUnitName();

    /* Locates everything that is exported and returns the matching PSI
     * elements. */
    List<RakuPsiDeclaration> getExports();
}