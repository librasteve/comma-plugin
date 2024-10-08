package edument.rakuidea.editor;

import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;
import edument.rakuidea.formatter.RakuCodeStyleSettings;

public class UnicodeReplacementHandlerTest extends CommaFixtureTestCase {
    public void testElem() {
        doTest("1 (elem<caret>", ')', "1 ∈");
    }

    public void testElemInComment() {
        doTest("# 1 (elem", ')', "# 1 (elem)");
    }

    public void testCont() {
        doTest("1 (cont", ')', "1 ∋");
    }

    public void testExclusion() {
        doTest("1 (-", ')', "1 ∖");
    }

    public void testComposition() {
        doTest("1 ", 'o', "1 ∘");
    }

    public void testAnd() {
        doTest("1 (&", ')', "1 ∩");
    }

    public void testOr() {
        doTest("1 (|", ')', "1 ∪");
    }

    public void testApproximatelyEqual() {
        doTest("1 =~", '=', "1 ≅");
    }

    public void testIncludes1() {
        doTest("1 (<", ')', "1 ⊂");
    }

    public void testIncludes2() {
        doTest("1 (>", ')', "1 ⊃");
    }

    public void testIncludesOrEqual1() {
        doTest("1 (<=", ')', "1 ⊆");
    }

    public void testIncludesOrEqual2() {
        doTest("1 (>=", ')', "1 ⊇");
    }

    public void testMultisetMultiplication() {
        doTest("1 (.", ')', "1 ⊍");
    }

    public void testMultisetAddition() {
        doTest("1 (+", ')', "1 ⊎");
    }

    public void testMultisetDifference() {
        doTest("1 (^", ')', "1 ⊖");
    }

    public void testNotElem() {
        doTest("1 !(elem", ')', "1 ∉");
    }

    public void testNotCont() {
        doTest("1 !(cont", ')', "1 ∌");
    }

    public void testNotIncluded() {
        doTest("1 !(<", ')', "1 ⊄");
    }

    public void testNotIncludes() {
        doTest("1 !(>", ')', "1 ⊅");
    }

    public void testNotIncludedOrEqual() {
        doTest("1 !(<=", ')', "1 ⊈");
    }

    public void testNotIncludesOrEqual() {
        doTest("1 !(>=", ')', "1 ⊉");
    }

    protected void doTest(String source, char type, String result) {
        CodeStyleSettingsManager.getInstance(getProject()).getMainProjectCodeStyle().getCustomSettings(RakuCodeStyleSettings.class).CONVERT_TO_UNICODE = true;
        myFixture.configureByText(RakuScriptFileType.INSTANCE, source);
        myFixture.getEditor().getCaretModel().moveToOffset(myFixture.getEditor().getDocument().getTextLength());
        myFixture.type(type);
        myFixture.checkResult(result);
        CodeStyleSettingsManager.getInstance(getProject()).getMainProjectCodeStyle().getCustomSettings(RakuCodeStyleSettings.class).CONVERT_TO_UNICODE = false;
    }
}
