package edument.perl6idea.parsing;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.psi.impl.Perl6FileImpl;
import edument.perl6idea.psi.impl.Perl6StatementImpl;
import edument.perl6idea.psi.impl.Perl6StatementListImpl;
import edument.perl6idea.psi.impl.Perl6UseStatementImpl;
import org.jetbrains.annotations.NotNull;

public class Perl6ParserDefinition implements ParserDefinition {
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new Perl6Lexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new Perl6Parser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return Perl6ElementTypes.FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        IElementType type = astNode.getElementType();
        if (type == Perl6ElementTypes.STATEMENT_LIST)
            return new Perl6StatementListImpl(astNode);
        if (type == Perl6ElementTypes.STATEMENT)
            return new Perl6StatementImpl(astNode);
        if (type == Perl6ElementTypes.USE_STATEMENT)
            return new Perl6UseStatementImpl(astNode);
        return null;
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new Perl6FileImpl(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
        return SpaceRequirements.MAY;
    }
}