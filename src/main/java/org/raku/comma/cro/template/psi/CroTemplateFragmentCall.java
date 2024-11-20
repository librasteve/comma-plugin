package org.raku.comma.cro.template.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface CroTemplateFragmentCall extends PsiNamedElement {
    PsiElement[] getCallArguments();
}
