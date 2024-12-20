package org.raku.comma.cro.template.parsing;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

public interface CroTemplateTokenTypes {
    IElementType ATTRIBUTE_EQUALS = new CroTemplateElementType("ATTRIBUTE_EQUALS");
    IElementType ATTRIBUTE_NAME = new CroTemplateElementType("ATTRIBUTE_NAME");
    IElementType ATTRIBUTE_QUOTE = new CroTemplateElementType("ATTRIBUTE_QUOTE");
    IElementType ATTRIBUTE_VALUE = new CroTemplateElementType("ATTRIBUTE_VALUE");
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    IElementType BOOL_LITERAL = new CroTemplateElementType("BOOL_LITERAL");
    IElementType CLOSE_ANGLE = new CroTemplateElementType("CLOSE_ANGLE");
    IElementType CLOSE_BRACKET = new CroTemplateElementType("CLOSE_BRACKET");
    IElementType CLOSE_CURLY = new CroTemplateElementType("CLOSE_CURLY");
    IElementType CLOSE_PAREN = new CroTemplateElementType("CLOSE_PAREN");
    IElementType COLON = new CroTemplateElementType("COLON");
    IElementType COMMA = new CroTemplateElementType("COMMA");
    IElementType COMMENT = new CroTemplateElementType("COMMENT");
    IElementType DECL_OPENER = new CroTemplateElementType("DECL_OPENER");
    IElementType DEFAULT_PARAMETER_SYNTAX = new CroTemplateElementType("DEFAULT_PARAMETER_SYNTAX");
    IElementType DOT = new CroTemplateElementType("DOT");
    IElementType END_OF_EXPR = new CroTemplateElementType("END_OF_EXPR");
    IElementType FRAGMENT_NAME = new CroTemplateElementType("FRAGMENT_NAME");
    IElementType HTML_COMMENT = new CroTemplateElementType("HTML_COMMENT");
    IElementType IDENTIFER = new CroTemplateElementType("IDENTIFER");
    IElementType INFIX = new CroTemplateElementType("INFIX");
    IElementType INT_LITERAL = new CroTemplateElementType("INT_LITERAL");
    IElementType LITERAL_KEY = new CroTemplateElementType("LITERAL_KEY");
    IElementType LITERAL_TAG_CLOSE = new CroTemplateElementType("LITERAL_TAG_CLOSE");
    IElementType LITERAL_TAG_NAME = new CroTemplateElementType("LITERAL_TAG_NAME");
    IElementType LITERAL_TAG_OPEN = new CroTemplateElementType("LITERAL_TAG_OPEN");
    IElementType LITERAL_TAG_SLASH = new CroTemplateElementType("LITERAL_TAG_SLASH");
    IElementType LITERAL_TEXT = new CroTemplateElementType("LITERAL_TEXT");
    IElementType MACRO_NAME = new CroTemplateElementType("MACRO_NAME");
    IElementType MODULE_NAME = new CroTemplateElementType("MODULE_NAME");
    IElementType NAMED_ARGUMENT_NAME = new CroTemplateElementType("NAMED_ARGUMENT_NAME");
    IElementType NAMED_ARGUMENT_SYNTAX = new CroTemplateElementType("NAMED_ARGUMENT_SYNTAX");
    IElementType NAMED_PARAMETER_SYNTAX = new CroTemplateElementType("NAMED_PARAMETER_SYNTAX");
    IElementType NUM_LITERAL = new CroTemplateElementType("NUM_LITERAL");
    IElementType OPEN_ANGLE = new CroTemplateElementType("OPEN_ANGLE");
    IElementType OPEN_BRACKET = new CroTemplateElementType("OPEN_BRACKET");
    IElementType OPEN_CURLY = new CroTemplateElementType("OPEN_CURLY");
    IElementType OPEN_PAREN = new CroTemplateElementType("OPEN_PAREN");
    IElementType PART_NAME = new CroTemplateElementType("PART_NAME");
    IElementType RAT_LITERAL = new CroTemplateElementType("RAT_LITERAL");
    IElementType STRING_QUOTE_SINGLE = new CroTemplateElementType("STRING_QUOTE_SINGLE");
    IElementType STRING_TEXT = new CroTemplateElementType("STRING_TEXT");
    IElementType SUB_NAME = new CroTemplateElementType("SUB_NAME");
    IElementType SYNTAX_WHITE_SPACE = new CroTemplateElementType("SYNTAX_WHITE_SPACE");
    IElementType TEMPLATE_TAG_APPLY_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_APPLY_SIGIL");
    IElementType TEMPLATE_TAG_CALL_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_CALL_SIGIL");
    IElementType TEMPLATE_TAG_CLOSE = new CroTemplateElementType("TEMPLATE_TAG_CLOSE");
    IElementType TEMPLATE_TAG_COND_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_COND_SIGIL");
    IElementType TEMPLATE_TAG_DECL_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_DECL_SIGIL");
    IElementType TEMPLATE_TAG_ELSE_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_ELSE_SIGIL");
    IElementType TEMPLATE_TAG_ITER_SIGIL = new CroTemplateElementType("TEMPLATE_TAG_ITER_SIGIL");
    IElementType TEMPLATE_TAG_OPEN = new CroTemplateElementType("TEMPLATE_TAG_OPEN");
    IElementType TEMPLATE_TAG_SLASH = new CroTemplateElementType("TEMPLATE_TAG_SLASH");
    IElementType VARIABLE_NAME = new CroTemplateElementType("VARIABLE_NAME");
}
