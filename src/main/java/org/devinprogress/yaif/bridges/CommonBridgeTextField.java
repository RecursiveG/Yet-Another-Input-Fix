package org.devinprogress.yaif.bridges;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.devinprogress.yaif.GuiStateManager;
import org.devinprogress.yaif.InputFieldWrapper;
import org.devinprogress.yaif.YetAnotherInputFix;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

public class CommonBridgeTextField extends BaseActionBridge {
    private GuiScreen scr = null;
    private GuiTextField txt = null;
    private InputFieldWrapper wrapper = null;
    private Method keyTypedMethod = null;

    public CommonBridgeTextField(GuiScreen screen, GuiTextField textField, InputFieldWrapper wrapper) {
        //YetAnotherInputFix.log("CommonBridgeTextField Initialized. %s",this);
        scr = screen;
        this.wrapper = wrapper;
        txt = textField;

        this.wrapper = wrapper;
        try {
            keyTypedMethod = scr.getClass().getDeclaredMethod(YetAnotherInputFix.ObfuscatedEnv ? "func_73869_a" : "keyTyped", char.class, int.class);
            keyTypedMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean needShow() {
        return keyTypedMethod != null;
    }

    @Override
    public void bindKeys(JTextField tf) {
        super.bindKeys(tf);

        bindKey(tf, KeyEvent.VK_ENTER, "enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(wrapper.getText());
                GuiStateManager.getInstance().TextFieldFocusChanged(scr, txt, false);
                wrapper.closeInputField();
            }
        });

        bindKey(tf, KeyEvent.VK_ESCAPE, "esc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(wrapper.getText());
                GuiStateManager.getInstance().TextFieldFocusChanged(scr, txt, false);
                wrapper.closeInputField();
            }
        });
        wrapper.setText(txt.getText());
        setListenDocumentEvent(tf);
    }

    @Override
    protected void textUpdated() {
        String str = wrapper.getText();
        int lim = txt.getMaxStringLength();
        final String finStr;
        if (str.length() > lim) {
            finStr = str.substring(0, lim);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textChangedByBridge = true;
                    wrapper.setText(finStr);
                }
            });
        } else {
            finStr = str;
        }
        dispatch(new Runnable() {
            @Override
            public void run() {
                try {
                    if (finStr.equals("")) {
                        txt.setText("X");
                        keyTypedMethod.invoke(scr, ' ', 14);
                    } else {
                        txt.setText(finStr.substring(0, finStr.length() - 1));
                        keyTypedMethod.invoke(scr, finStr.charAt(finStr.length() - 1), -1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void postGuiInit() {
        textChangedByBridge = true;
        wrapper.setText(txt.getText());
    }
}