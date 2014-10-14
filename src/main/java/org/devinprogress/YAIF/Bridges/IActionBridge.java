package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import javax.swing.*;

/**
 * Created by recursiveg on 14-9-11.
 */
public interface IActionBridge {
    //public IActionBridge(GuiTextField textField,GuiScreen screen,InputFieldWrapper wrapper);
    enum ActionFeedback{
        Nothing,
        Clean,
        Quit,
        SetText
    }

    //TODO: Maybe it's a better idea to make wrapper.txtField public?
    public ActionFeedback onEnter(final JTextField txt);
    public ActionFeedback onEsc(final JTextField txt);
    public ActionFeedback onChange(final JTextField txt);
    public ActionFeedback onTab(final JTextField txt);
    public ActionFeedback onUp(final JTextField txt);
    public ActionFeedback onDown(final JTextField txt);
    public ActionFeedback onBackspace(final JTextField txt);
    public boolean sameAs(GuiScreen screen,GuiTextField txtField);
}
