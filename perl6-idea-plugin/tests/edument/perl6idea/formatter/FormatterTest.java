package edument.perl6idea.formatter;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import edument.perl6idea.CommaFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class FormatterTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "perl6-idea-plugin/testData/formatter";
  }

    public void testBasicFormatting() {
        myFixture.configureByFiles("basic.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("basic.out.p6");
    }

    public void testAssortedFormatting() {
        myFixture.configureByFiles("assorted.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("assorted.out.p6");
    }

    public void testBasicGrammarFormatting() {
        myFixture.configureByFiles("grammar-basic.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("grammar-basic.out.p6");
    }

    public void testContinuationAfterBlock() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "{\n\n}<caret>");
        CommandProcessor.getInstance().executeCommand(getProject(), () -> {
            EditorActionManager actionManager = EditorActionManager.getInstance();
            EditorActionHandler actionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
            actionHandler.execute(myFixture.getEditor(), null, DataManager.getInstance().getDataContextFromFocus().getResult());
        }, "", null);
        myFixture.checkResult("{\n\n}\n<caret>");
    }

    public void testLineBreakingOfStatements() {
        myFixture.configureByFiles("break-lines.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("break-lines.out.p6");
    }

    public void testLineBreakingOfBlocks() {
        myFixture.configureByFiles("blocks.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("blocks.out.p6");
    }

    public void testRemoveSpaceBeforeSemi() {
        myFixture.configureByFiles("space-before-semi.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("space-before-semi.out.p6");
    }

    public void testMultilineHashFormatting() {
        myFixture.configureByFiles("hash.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("hash.out.p6");
    }

    /* TODO Would be ideal to get this one to work also. */
    /*public void testMultilineHashWithMultilineValueFormatting() {
        myFixture.configureByFiles("hash-multiline-values.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("hash-multiline-values.out.p6");
    }*/

    public void testMultilineArrayFormatting() {
        myFixture.configureByFiles("array.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("array.out.p6");
    }

    public void testTrailingCommaInArrayAndHashFormatting() {
        myFixture.configureByFiles("trailing-comma.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("trailing-comma.out.p6");
    }

    public void testCommentsNotBrokenByFormatting() {
        myFixture.configureByFiles("comments-left-intact.in.p6");
        WriteCommandAction.runWriteCommandAction(null, () -> {
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(myFixture.getProject());
            PsiFile file = myFixture.getFile();
            codeStyleManager.reformatText(file, 0, file.getTextLength());
        });
        myFixture.checkResultByFile("comments-left-intact.out.p6");
    }
}
