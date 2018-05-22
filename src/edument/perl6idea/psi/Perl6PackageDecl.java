package edument.perl6idea.psi;

import com.intellij.psi.StubBasedPsiElement;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;

public interface Perl6PackageDecl extends Perl6PsiScope, Perl6PsiDeclaration,
                                          StubBasedPsiElement<Perl6PackageDeclStub>,
                                          Perl6IndexableType {
    String getPackageKind();
    String getPackageName();
    Perl6RoutineDecl[] privateMethods();
    void contributeNestedPackagesWithPrefix(Perl6SymbolCollector collector, String prefix);
}
