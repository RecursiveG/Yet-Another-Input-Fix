package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import org.devinprogress.YAIF.InputFieldWrapper;
import org.devinprogress.YAIF.YetAnotherInputFix;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by recursiveg on 14-11-12.
 */
public class CreativeInventoryBridge extends BaseActionBridge{
    private GuiContainerCreative gui;
    private GuiTextField searchField;
    private InputFieldWrapper wrapper;

    public CreativeInventoryBridge(GuiContainerCreative gui,GuiTextField tf,InputFieldWrapper w){
        this.gui=gui;
        searchField=tf;
        wrapper=w;
    }

    @Override
    public boolean needShow(){
        return true;
    }

    @Override
    public void bindKeys(JTextField tf){
        super.bindKeys(tf);

        bindKey(tf, KeyEvent.VK_ESCAPE, "esc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                YetAnotherInputFix.log("CreativeInventoryBridge ESC Pressed");
                wrapper.bridgeQuit();
            }
        });

        setListenDocumentEvent(tf);
    }

    @Override
    protected void textUpdated(){
        final String str=wrapper.getText();
        if(str.length()>15){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textChangedByBridge=true;
                    wrapper.setText(str.substring(0,15));
                }
            });
            searchField.setText(str.substring(0,15));
        }else {
            searchField.setText(str);
        }
        //gui.updateCreativeSearch();
    }
}
