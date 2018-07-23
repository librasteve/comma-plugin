package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

public interface Perl6Variable extends Perl6PsiElement {

    PsiElement getVariableToken();

    default String getVariableName() {
        PsiElement varToken = getVariableToken();
        return varToken != null ? varToken.getText() : null;
    }

    default char getSigil() {
        switch (getText().charAt(0)) {
            case '$': return '$';
            case '@': return '@';
            case '%': return '%';
            case '&': return '&';
            default:  return ' ';
        }
    }

    default char getTwigil() {
        if (getText().length() < 2) return ' ';
        switch (getText().charAt(1)) {
            case '*': return '*';
            case '?': return '?';
            case '!': return '!';
            case '^': return '^';
            case ':': return ':';
            case '=': return '=';
            case '~': return '~';
            case '.': return '.';
            default:  return ' ';
        }
    }
}