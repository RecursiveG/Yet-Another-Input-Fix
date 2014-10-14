package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.devinprogress.YAIF.InputFieldWrapper;

import javax.swing.*;

/**
 * Created by recursiveg on 14-9-11.
 */
public class DebugBridge implements IActionBridge {

    public DebugBridge(GuiTextField textField,GuiScreen screen,InputFieldWrapper wrapper){

    }
    @Override
    public ActionFeedback onEnter(JTextField txt) {
        System.out.println("[Debug Bridge][onEnter]"+txt.getText());
        return ActionFeedback.Quit;
    }

    @Override
    public ActionFeedback onEsc(JTextField txt) {
        System.out.println("[Debug Bridge][onEsc]"+txt.getText());
        return ActionFeedback.Quit;
    }

    @Override
    public ActionFeedback onChange(JTextField txt) {
        System.out.println("[Debug Bridge][onChange]"+txt.getText());
        return ActionFeedback.Nothing;
    }

    @Override
    public ActionFeedback onTab(JTextField txt) {
        System.out.println("[Debug Bridge][onTab]"+txt.getText());
        return ActionFeedback.Nothing;
    }

    @Override
    public ActionFeedback onUp(JTextField txt) {
        return null;
    }

    @Override
    public ActionFeedback onDown(JTextField txt) {
        return null;
    }

    @Override
    public ActionFeedback onBackspace(JTextField txt) {
        return null;
    }

    @Override
    public boolean sameAs(GuiScreen screen, GuiTextField txtField) {
        return false;
    }
}
