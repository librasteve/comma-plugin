package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6Signature;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalPerl6Signature extends Perl6ExternalPsiElement implements Perl6Signature {
    private Perl6Parameter[] myParameters;

    public ExternalPerl6Signature(Project project, PsiElement parent, JSONObject signature) {
        myProject = project;
        myParent = parent;
        JSONArray params = signature.getJSONArray("p");
        List<String> paramLines = new ArrayList<>();
        for (Object param : params) {
            if (param instanceof String)
                paramLines.add((String)param);
        }
        myParameters = paramLines.stream().map(n -> new ExternalPerl6Parameter(project, parent, n)).toArray(ExternalPerl6Parameter[]::new);
    }

    @Override
    public String summary(String retType) {
        return String.join(", ", Arrays.stream(myParameters).map(p -> p.summary()).toArray(String[]::new)) + "--> " + retType;
    }

    @Override
    public Perl6Parameter[] getParameters() {
        return myParameters;
    }
}
