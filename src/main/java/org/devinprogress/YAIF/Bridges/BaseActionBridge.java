package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import javax.swing.*;

/**
 * Created by recursiveg on 14-9-11.
 */
public abstract class BaseActionBridge {
    public void bindKeys(JTextField textField){
        textField.getInputMap().clear();
        textField.getActionMap().clear();
    }
    public void unlink(){ }
    public void onTabComplete(GuiChat chatScreen){ }
}
