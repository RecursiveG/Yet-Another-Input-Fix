package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import org.devinprogress.YAIF.InputFieldWrapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

public class EditSignBridge extends BaseActionBridge {
    private GuiEditSign gui;
    private InputFieldWrapper wrapper;
    private int currentLine=0;
    private TileEntitySign sign;
    public EditSignBridge(GuiEditSign gui,InputFieldWrapper w){
        this.gui=gui;
        this.wrapper=w;
        currentLine=0;
        sign=gui.tileSign;
    }

    @Override
    public boolean needShow(){
        return true;
    }

    @Override
    public void bindKeys(JTextField tf){
        super.bindKeys(tf);

        bindKey(tf, KeyEvent.VK_ENTER,"enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentLine=currentLine+1&3;
                gui.editLine=currentLine;
                textChangedByBridge=true;
                wrapper.setText(sign.signText[currentLine]);
            }
        });

        bindKey(tf, KeyEvent.VK_ESCAPE, "esc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //YetAnotherInputFix.log("GuiSignBridge ESC Pressed");
                wrapper.bridgeQuit();
            }
        });

        bindKey(tf,KeyEvent.VK_UP,"up",new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentLine=currentLine-1&3;
                gui.editLine=currentLine;
                textChangedByBridge=true;
                wrapper.setText(sign.signText[currentLine]);
            }
        });

        bindKey(tf,KeyEvent.VK_DOWN,"down",new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentLine=currentLine+1&3;
                gui.editLine=currentLine;
                textChangedByBridge=true;
                wrapper.setText(sign.signText[currentLine]);
            }
        });

        setListenDocumentEvent(tf);
    }

    @Override
    protected void textUpdated(){
        //YetAnotherInputFix.log("EditSignBridge textUpdate Invoked");
        final String str=wrapper.getText();
        if(str.length()>15){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textChangedByBridge=true;
                    wrapper.setText(str.substring(0,15));
                }
            });
            sign.signText[currentLine]=str.substring(0,15);
        }else {
            sign.signText[currentLine]=str;
        }
    }
}
