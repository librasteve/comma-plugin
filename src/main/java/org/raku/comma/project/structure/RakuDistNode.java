package org.raku.comma.project.structure;

import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.ui.NamedConfigurable;
import org.jetbrains.annotations.NotNull;

public class RakuDistNode extends MasterDetailsComponent.MyNode {
    public RakuDistNode(@NotNull NamedConfigurable userObject) {
        super(userObject);
    }
}