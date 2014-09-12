package org.devinprogress.inputfix;

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
    public ActionFeedback onEnter(final JTextField txt);
    public ActionFeedback onEsc(final JTextField txt);
    public ActionFeedback onChanged(final JTextField txt);
    public ActionFeedback onTab(final JTextField txt);
}
