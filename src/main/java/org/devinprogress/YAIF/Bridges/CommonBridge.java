package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.devinprogress.YAIF.InputFieldWrapper;

import javax.swing.*;

/**
 * Created by recursiveg on 14-9-11.
 */
public class CommonBridge implements IActionBridge{
    private GuiTextField txt=null;

    public CommonBridge(GuiTextField textField,InputFieldWrapper wrapper){
        txt=textField;
        wrapper.DoActions(ActionFeedback.SetText,txt.getText());

    }
    @Override
    public IActionBridge.ActionFeedback onEnter(JTextField txt) { //send msg
        this.txt.setText(txt.getText());
        return IActionBridge.ActionFeedback.Quit;
    }

    @Override
    public IActionBridge.ActionFeedback onEsc(JTextField txt) {
        return IActionBridge.ActionFeedback.Quit;
    }

    @Override
    public IActionBridge.ActionFeedback onChange(JTextField txt) {
        this.txt.setText(txt.getText());
        return IActionBridge.ActionFeedback.Nothing;
    }

    @Override
    public IActionBridge.ActionFeedback onTab(JTextField txt) {
        return IActionBridge.ActionFeedback.Nothing;
    }

    @Override
    public ActionFeedback onUp(JTextField txt) {
        return null;
    }

    @Override
    public ActionFeedback onDown(JTextField txt) {
        return null;
    }
}
