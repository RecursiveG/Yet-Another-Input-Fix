package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import org.devinprogress.YAIF.InputFieldWrapper;

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
    }
    @Override
    public ActionFeedback onEnter(JTextField txt) {
        currentLine=currentLine+1&3;
        wrapper.setTextNoEvent(sign.signText[currentLine]);
        return null;
    }

    @Override
    public ActionFeedback onEsc(JTextField txt) {
        return ActionFeedback.Quit;
    }

    @Override
    public ActionFeedback onChange(JTextField txt) {
        sign.signText[currentLine]=txt.getText();
        return null;
    }

    @Override
    public ActionFeedback onTab(JTextField txt) {
        return null;
    }

    @Override
    public ActionFeedback onUp(JTextField txt) {
        currentLine=currentLine-1&3;
        wrapper.setTextNoEvent(sign.signText[currentLine]);
        return null;
    }

    @Override
    public ActionFeedback onDown(JTextField txt) {
        currentLine=currentLine+1&3;
        wrapper.setTextNoEvent(sign.signText[currentLine]);
        return null;
    }

    @Override
    public boolean sameAs(GuiScreen screen, GuiTextField txtField) {
        return screen==gui;
    }
}
