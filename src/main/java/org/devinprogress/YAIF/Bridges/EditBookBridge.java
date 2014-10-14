package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagList;
import org.devinprogress.YAIF.YetAnotherInputFix;

import javax.swing.*;
import java.lang.reflect.Field;

/**
 * Created by recursiveg on 14-10-2.
 */
public class EditBookBridge implements IActionBridge {
    private boolean canedit=false;
    private Field F_isEditingTitle =null;
    private Field F_PagesNBT=null;
    private GuiScreenBook sc=null;

    private NBTTagList pages;

    public EditBookBridge(GuiScreenBook sc){
        try{
            Field f=sc.getClass().getField(YetAnotherInputFix.ObfuscatedEnv?"field_146475_i":"bookIsUnsigned");
            f.setAccessible(true); canedit=(Boolean)f.get(sc);

            Field modified=sc.getClass().getField("field_146481_r");
            modified.setAccessible(true); modified.set(sc,true);

            F_isEditingTitle =sc.getClass().getField("field_146480_s");
            F_isEditingTitle.setAccessible(true);

            F_PagesNBT=sc.getClass().getField(YetAnotherInputFix.ObfuscatedEnv?"field_146483_y":"bookPages");
            F_PagesNBT.setAccessible(true);

            this.sc=sc;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ActionFeedback onEnter(JTextField txt) {
        return null;
    }

    @Override
    public ActionFeedback onEsc(JTextField txt) {
        return null;
    }

    @Override
    public ActionFeedback onChange(JTextField txt) {
        return null;
    }

    @Override
    public ActionFeedback onTab(JTextField txt) {
        return null;
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
