package org.raku.parsing;

import com.intellij.psi.tree.IElementType;
import org.raku.RakuLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PrecInfoToken extends IElementType {
    private final String prec;
    private final String subPrec;
    private final String assoc;
    private final Boolean fake;

    private PrecInfoToken(@NotNull String prec, String subPrec, String assoc, Boolean fake) {
        super("PrecInfo: " + prec + ", " + subPrec + ", " + assoc + ", " + fake,
              RakuLanguage.getInstance());
        this.prec = prec;
        this.subPrec = subPrec;
        this.assoc = assoc;
        this.fake = fake;
    }

    public String prec() {
        return prec;
    }

    public String subPrec() {
        return subPrec;
    }

    public String assoc() {
        return assoc;
    }

    public Boolean fake() { return fake; }

    private static final Map<String, PrecInfoToken> cache = new HashMap<>();

    public static PrecInfoToken tokenFor(String prec, String subPrec, String assoc, Boolean fake) {
        String key = prec + "\0" + subPrec + "\0" + assoc + "\0" + fake;
        synchronized (cache) {
            PrecInfoToken found = cache.get(key);
            if (found == null) {
                found = new PrecInfoToken(prec, subPrec, assoc, fake);
                cache.put(key, found);
            }
            return found;
        }
    }
}
