package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import org.devinprogress.YAIF.InputFieldWrapper;
import org.devinprogress.YAIF.YetAnotherInputFix;

import javax.swing.*;
import java.lang.reflect.Field;

/**
 * Created by recursiveg on 14-9-21.
 */
public class EditSignBridge implements IActionBridge {
    private GuiEditSign gui;
    private InputFieldWrapper wrapper;
    private int currentLine=0;
    private TileEntitySign sign;
    private Field currentLineField=null;
    public EditSignBridge(GuiEditSign gui,InputFieldWrapper w){
        this.gui=gui;
        this.wrapper=w;
        currentLine=0;
        w.setTextNoEvent("");
        //TODO: USE AccessTransformer
        for(Field f:gui.getClass().getDeclaredFields())
            if(f.getType().equals(TileEntitySign.class)){
                try {
                    f.setAccessible(true);
                    sign = (TileEntitySign)f.get(gui);
                    break;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        //TODO: modification needed of updated
        try {
            currentLineField = gui.getClass().getDeclaredField(YetAnotherInputFix.ObfuscatedEnv ? "field_146851_h" : "editLine");
            currentLineField.setAccessible(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public ActionFeedback onEnter(JTextField txt) {
        currentLine=currentLine+1&3;
        try{
            currentLineField.set(gui,currentLine);
        }catch(Exception e){
            e.printStackTrace();
        }
        wrapper.setTextNoEvent(sign.signText[currentLine]);
        return null;
    }

    @Override
    public ActionFeedback onEsc(JTextField txt) {
        //SetInGameFocus will close the GuiEditSign.
        return ActionFeedback.Quit;
    }

    @Override
    public ActionFeedback onChange(JTextField txt) {
        if(txt.getText().length()<=15)
            sign.signText[currentLine]=txt.getText();
        else
            sign.signText[currentLine]=txt.getText().substring(0,15);
        return null;
    }

    @Override
    public ActionFeedback onTab(JTextField txt) {
        return null;
    }

    @Override
    public ActionFeedback onUp(JTextField txt) {
        currentLine=currentLine-1&3;
        try{
            currentLineField.set(gui,currentLine);
        }catch(Exception e){
            e.printStackTrace();
        }
        wrapper.setTextNoEvent(sign.signText[currentLine]);
        return null;
    }

    @Override
    public ActionFeedback onDown(JTextField txt) {
        currentLine=currentLine+1&3;
        try{
            currentLineField.set(gui,currentLine);
        }catch(Exception e){
            e.printStackTrace();
        }
        wrapper.setTextNoEvent(sign.signText[currentLine]);
        return null;
    }

    @Override
    public ActionFeedback onBackspace(JTextField txt) {
        String str2=txt.getText();
        str2=str2.substring(0,str2.length()-1);
        wrapper.setTextNoEvent(str2);
        return onChange(txt);
    }

    @Override
    public boolean sameAs(GuiScreen screen, GuiTextField txtField) {
        return screen==gui;
    }
}
