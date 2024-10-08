package org.raku.language;

import com.intellij.openapi.components.*;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
@State(name = "org.raku.language.RakuLanguageVersionService", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public final class RakuLanguageVersionService implements PersistentStateComponent<Element> {
    private RakuLanguageVersion myVersion = RakuLanguageVersion.D;
    private boolean myIsExplicit = true;

    public RakuLanguageVersion getVersion() {
        return myVersion;
    }

    public void setVersion(RakuLanguageVersion version) {
        myVersion = version;
    }

    public boolean getIsExplicit() {
        return myIsExplicit;
    }

    public void setExplicit(boolean explicit) {
        myIsExplicit = explicit;
    }

    @Override
    public @NotNull Element getState() {
        Element el = new Element("language");
        el.setAttribute("version", myVersion.name());
        el.setAttribute("explicit-for-new", String.valueOf(myIsExplicit));
        return el;
    }

    @Override
    public void loadState(@NotNull Element state) {
        String version = state.getAttributeValue("version");
        if (version != null) {
            myVersion = RakuLanguageVersion.valueOf(version);
        } else {
            myVersion = RakuLanguageVersion.D;
        }
        String is_explicit = state.getAttributeValue("explicit-for-new");
        myIsExplicit = is_explicit == null || is_explicit.equals("true");
    }
}
