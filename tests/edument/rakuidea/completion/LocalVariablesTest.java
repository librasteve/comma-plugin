package edument.rakuidea.completion;

import com.intellij.codeInsight.completion.CompletionType;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.filetypes.RakuScriptFileType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocalVariablesTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/codeInsight/localVariables";
    }

    public void testCompletion() {
        myFixture.configureByFile("DefaultTestDataSigil.pm6");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Collections.singletonList("$a"));

        myFixture.type("a;\n@");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertTrue(vars.containsAll(Arrays.asList("@b1", "@b2")));

        myFixture.type("b1;\n%cooo");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("%coool1", "%coool2"));
        assertEquals(2, vars.size());

        myFixture.type("1;\n&doooooooo");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Arrays.asList("&dooooooood1", "&dooooooood2"));
        assertEquals(2, vars.size());
    }

    public void testRoutineVariablesFromSetting() {
        doTest("&sec<caret>", "&sec", "&sech");
    }

    public void testFormalVariables() {
        doTest("sub foo { $^a + $<caret> }", "$a");
        doTest("sub foo { $:a + $<caret> }", "$a");
    }

    public void testRoleParameterVariable() {
        doTest("role P[$name1, $name2] { method m { $na<caret> } }",
               "$name1", "$name2");
    }

    public void testAttributeCompletionWithInnerClasses() {
        doTest("class C { has $!abc; class Inner { has $!xyz;  method m() { say $!<caret> } } }",
               "$!", "$!xyz");
    }

    public void testUnitSubProvidesParamsToOuterScope() {
        myFixture.configureByText(RakuScriptFileType.INSTANCE,
                                  "unit sub MAIN($cool); say $co<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Collections.singletonList("$cool"));

        myFixture.configureByText(RakuScriptFileType.INSTANCE,
                                  "unit sub MAIN(Int :$cool); say $co<caret>");
        myFixture.complete(CompletionType.BASIC, 1);
        vars = myFixture.getLookupElementStrings();
        assertNotNull(vars);
        assertContainsElements(vars, Collections.singletonList("$cool"));
    }

    public void testCommentDoesNotThrow() {
        doTest("my $a = #`( comment )\n10; say $a.<caret>", ".abs");
    }

    public void testRegexProviding() {
        doTest("'test' ~~ /(\\w) (\\w) $<testtt> = \\w /; say $<caret>", "$0", "$1", "$<testtt>");
        doTest("/(\\w)/; say $<caret>", "$0");
        doNegativeTest("'' ~~ /(\\w)/; sub t { say $<caret> }", "$0");
        doNegativeTest("'test' ~~ /(\\w)/; given {} -> $/ { $<caret> }", "$0");
        doNegativeTest("'test' ~~ /(\\w)/; my $/; $<caret>", "$0");
        doTest("'test' ~~ /$<let>=(\\w)/; 'test' ~~ /$<let2>=(\\w)/; $<caret>", "$<let2>");
        doNegativeTest("'test' ~~ /$<let>=(\\w)/; 'test' ~~ /$<let2>=(\\w)/; $<caret>", "$<let>");
        doTest("'abc123'.subst(/ $<let> = \\d /, { $<caret> })", "$<let>");
        doTest("'abc123' ~~ s/$<let>=[<:Ll>+]/$<caret>/;", "$<let>");
        doTest("my $var = /(\\w) (\\w) $<testtt> = \\w /; 'hehe' ~~ $var; say $<caret>", "$0", "$1", "$<testtt>");
        doTest("sub a {}; 'foo' ~~ /(.)$<a>=[.]/;sub foo() {\"foo\" ~~ /$<b>=./;};$<caret>", "$<a>");
        doTest("'foo' ~~ /(.)$<a>=[.]/;sub foo() {\"foo\" ~~ /$<b>=./;};$<caret>", "$<a>");
        doNegativeTest("'foo' ~~ /(.)$<a>=[.]/;sub foo() {\"foo\" ~~ /$<b>=./;};$<caret>", "$<b>");
        doTest("'foo' ~~ /<a>/; $<caret>", "$<a>");
        doTest("/<alnum> <.hehe> { $<caret> }/", "$<alnum>");
        doTest("/<local=.alnum> { $<caret> }/", "$<local>");
        doNegativeTest("/<alnum> <.hehe> { $<caret> }/", "$<hehe>");
        doNegativeTest("/ <?after 'a'> { say $<<caret> } /", "$<?after 'a'>");

        // Regressions
        doTest("grammar G { token foo:sym<bar> { (.) $<foo> = \\w } }; class G { method foo:sym<bar>($/) { say $<caret> } }", "$0", "$<foo>");
        doNegativeTest("my $foo = \"omg42 wow\" ~~ /[<:L>\\d] (x(y)) (zzz)/; say $<caret>", "$2");
        doTest("'foo' ~~ /$<a>=[<foo> <bar>]/; $<caret>", "$<a>", "$<foo>", "$<bar>");
        doNegativeTest("'foo' ~~ /$<a>=(<foo> <bar>)/; $<caret>", "$<foo>", "$<bar>");
    }

    public void testIntegration() {
        doTest("'a' ~~ $foo.<caret>");
        doTest("'a' ~~ /{ $42.<caret> }");
        doTest("'a' ~~ /{ $foo.<caret> }");
    }

    private void doTest(String text, String... elems) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, text);
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertContainsElements(methods, elems);
    }

    private void doNegativeTest(String text, String... elems) {
        myFixture.configureByText(RakuScriptFileType.INSTANCE, text);
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> methods = myFixture.getLookupElementStrings();
        assertNotNull(methods);
        assertDoesntContain(methods, elems);
    }
}