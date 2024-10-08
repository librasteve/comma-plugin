package org.raku.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.raku.utils.Patterns;
import org.raku.utils.RakuProjectType;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.util.HashMap;
import java.util.Map;

public class RakuModuleWizardStep extends ModuleWizardStep {
    public static final String SCRIPT_NAME = "SCRIPT_NAME";
    public static final String MODULE_NAME = "MODULE_NAME";
    public static final String ENTRY_POINT_NAME = "ENTRY_POINT_NAME";
    public static final String WEBSOCKET_SUPPORT = "WEBSOCKET_SUPPORT";
    public static final String TEMPLATE_SUPPORT = "TEMPLATE_SUPPORT";

    private final RakuModuleBuilder builder;
    private RakuProjectType currentType;
    private JPanel myMainPanel;
    // Script fields
    private final JTextField myScriptName = new JBTextField(40);

    // Module fields
    private final JTextField myModuleName = new JBTextField(40);

    // Application fields
    private final JTextField myEntryName = new JBTextField(40);

    // Cro application fields
    private final JCheckBox myWebsocketSupport = new JCheckBox();
    private final JCheckBox myTemplatingSupport = new JCheckBox();

    RakuModuleWizardStep(RakuModuleBuilder builder) {
        this.builder = builder;
        this.currentType = builder.getRakuModuleType();
        updateInputs();
    }

    private void updateInputs() {
        myMainPanel = new JBPanel<>();
        Border border = myMainPanel.getBorder();
        Border margin = JBUI.Borders.empty(10);
        myMainPanel.setBorder(new CompoundBorder(border, margin));
        myMainPanel.setLayout(new MigLayout());
        switch (builder.getRakuModuleType()) {
            case RAKU_SCRIPT:
                myMainPanel.add(new JLabel("Script name"));
                myMainPanel.add(myScriptName, "wrap");
                break;
            case RAKU_MODULE: {
                myMainPanel.add(new JLabel("Module name"));
                myMainPanel.add(myModuleName, "wrap");
                break;
            }
            case RAKU_APPLICATION: {
                myMainPanel.add(new JLabel("Module name"));
                myMainPanel.add(myModuleName, "wrap");
                myMainPanel.add(new JLabel("Entry point name"));
                myMainPanel.add(myEntryName);
                break;
            }
            default: {
                myMainPanel.add(new JLabel("Module name"));
                myMainPanel.add(myModuleName, "wrap");
                myMainPanel.add(new JLabel("WebSocket support"));
                myMainPanel.add(myWebsocketSupport, "wrap");
                myMainPanel.add(new JLabel("Templating support"));
                myMainPanel.add(myTemplatingSupport, "wrap");
                break;
            }
        }
    }

    @Override
    public JComponent getComponent() {
        // If the module type was changed, we need to re-draw UI
        if (builder.getRakuModuleType() != currentType) {
            currentType = builder.getRakuModuleType();
            updateInputs();
        }
        return myMainPanel;
    }

    @Override
    public boolean validate() throws ConfigurationException {
        switch (builder.getRakuModuleType()) {
            case RAKU_SCRIPT:
                checkScriptName();
                break;
            case RAKU_MODULE:
            case CRO_WEB_APPLICATION:
                checkModuleName();
                break;
            case RAKU_APPLICATION:
                checkModuleName();
                checkEntryPointName();
                break;
        }
        return true;
    }

    private void checkEntryPointName() throws ConfigurationException {
        String name = myEntryName.getText();
        if (!name.matches(Patterns.ENTRY_POINT_PATTERN)) {
            throw new ConfigurationException("Entry point name is incorrect. Examples: `foo`, `foo.p6`, `foo.pl6`.");
        }
    }

    private void checkModuleName() throws ConfigurationException {
        String name = myModuleName.getText();
        if (!name.matches(Patterns.MODULE_PATTERN)) {
            throw new ConfigurationException("Module name is incorrect. Examples: `Foo`, `Foo::Bar`, `foo::Bar`");
        }
    }

    private void checkScriptName() throws ConfigurationException {
        String name = myScriptName.getText();
        if (!name.matches(Patterns.SCRIPT_PATTERN)) {
            throw new ConfigurationException("Script name must contain only letters and numbers");
        }
    }

    @Override
    public void updateDataModel() {
        Map<String, String> formData = new HashMap<>();
        formData.put(SCRIPT_NAME, myScriptName.getText());
        formData.put(MODULE_NAME, myModuleName.getText());
        formData.put(ENTRY_POINT_NAME, myEntryName.getText());
        formData.put(WEBSOCKET_SUPPORT, String.valueOf(myWebsocketSupport.isSelected()));
        formData.put(TEMPLATE_SUPPORT, String.valueOf(myTemplatingSupport.isSelected()));
        builder.updateLocalBuilder(formData);
    }
}
