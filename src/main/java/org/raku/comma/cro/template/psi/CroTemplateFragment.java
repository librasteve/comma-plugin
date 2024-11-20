package org.raku.comma.cro.template.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import org.raku.comma.cro.template.psi.stub.CroTemplateFragmentStub;

public interface CroTemplateFragment extends StubBasedPsiElement<CroTemplateFragmentStub>, NavigatablePsiElement, PsiNamedElement, Scope, Declaration {
}
