package edument.perl6idea.parsing;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

public interface Perl6TokenTypes {
    IElementType INTEGER_LITERAL = new Perl6ElementType("INTEGER_LITERAL");
    IElementType NUMBER_LITERAL = new Perl6ElementType("NUMBER_LITERAL");
    IElementType STATEMENT_TERMINATOR = new Perl6ElementType("STATEMENT_TERMINATOR");
    IElementType PARENTHESES = new Perl6ElementType("PARENTHESES");
    IElementType STATEMENT_CONTROL = new Perl6ElementType("STATEMENT_CONTROL");
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    IElementType INFIX = new Perl6ElementType("INFIX");
    IElementType VARIABLE = new Perl6ElementType("VARIABLE");
    IElementType INVOCANT_MARKER = new Perl6ElementType("INVOCANT_MARKER");
    IElementType POSTFIX = new Perl6ElementType("POSTFIX");
    IElementType SUB_CALL_NAME = new Perl6ElementType("SUB_CALL_NAME");
    IElementType NAME = new Perl6ElementType("NAME");
    IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
    IElementType METHOD_CALL_NAME = new Perl6ElementType("METHOD_CALL_NAME");
    IElementType SCOPE_DECLARATOR = new Perl6ElementType("SCOPE_DECLARATOR");
    IElementType PREFIX = new Perl6ElementType("PREFIX");
    IElementType COMMENT = new Perl6ElementType("COMMENT");
    IElementType RAT_LITERAL = new Perl6ElementType("RAT_LITERAL");
    IElementType METHOD_CALL_OPERATOR = new Perl6ElementType("METHOD_CALL_OPERATOR");
}
