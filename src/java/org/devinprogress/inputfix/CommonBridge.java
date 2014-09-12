package org.devinprogress.inputfix;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import javax.swing.*;

/**
 * Created by recursiveg on 14-9-11.
 */
public class CommonBridge implements IActionBridge{
    private GuiTextField txt=null;

    public CommonBridge(GuiTextField textField,GuiScreen screen,InputFieldWrapper wrapper){
        txt=textField;
        wrapper.DoActions(ActionFeedback.SetText,txt.getText());

    }
    @Override
    public IActionBridge.ActionFeedback onEnter(JTextField txt) {
        //System.out.println("[Debug Bridge][onEnter]"+txt.getText());
        this.txt.setText(txt.getText());
        return IActionBridge.ActionFeedback.Quit;
    }

    @Override
    public IActionBridge.ActionFeedback onEsc(JTextField txt) {
        //System.out.println("[Debug Bridge][onEsc]"+txt.getText());
        return IActionBridge.ActionFeedback.Quit;
    }

    @Override
    public IActionBridge.ActionFeedback onChanged(JTextField txt) {
        //System.out.println("[Debug Bridge][onChange]"+txt.getText());
        this.txt.setText(txt.getText());
        return IActionBridge.ActionFeedback.Nothing;
    }

    @Override
    public IActionBridge.ActionFeedback onTab(JTextField txt) {
        //System.out.println("[Debug Bridge][onTab]"+txt.getText());
        return IActionBridge.ActionFeedback.Nothing;
    }
}
