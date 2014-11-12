package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.devinprogress.YAIF.InputFieldWrapper;
import org.devinprogress.YAIF.YetAnotherInputFix;

import javax.swing.*;
import java.lang.reflect.Method;

/**
 * Created by recursiveg on 14-9-11.
 */
public class CommonBridgeTextField extends BaseActionBridge {
    private GuiScreen scr=null;
    private GuiTextField txt=null;
    private InputFieldWrapper wrapper=null;
    private Method M_keyTyped=null;

    public CommonBridgeTextField(GuiScreen screen,GuiTextField textField,InputFieldWrapper wrapper){
        YetAnotherInputFix.log("CommonBridgeTextField Initialized. %s",this);
        scr=screen;
        this.wrapper=wrapper;
        try{
            M_keyTyped=scr.getClass().getDeclaredMethod(YetAnotherInputFix.ObfuscatedEnv?"func_73869_a":"keyTyped",char.class,int.class);
            M_keyTyped.setAccessible(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
    @Override
    public BaseActionBridge.ActionFeedback onEnter(JTextField txt) { //send msg
        try{
            String str=txt.getText();
            if(str.length()==0){
                M_keyTyped.invoke(scr,'\n',28);
            }else{
                for(int i=0;i<str.length();i++){
                    M_keyTyped.invoke(scr,str.charAt(i),-1);
                }
                wrapper.setTextNoEvent("");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BaseActionBridge.ActionFeedback onEsc(JTextField txt) {
        return BaseActionBridge.ActionFeedback.Nothing;
    }

    @Override
    public BaseActionBridge.ActionFeedback onChange(JTextField txt) {
        return BaseActionBridge.ActionFeedback.Nothing;
    }

    @Override
    public BaseActionBridge.ActionFeedback onTab(JTextField txt) {
        return BaseActionBridge.ActionFeedback.Nothing;
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
        try{
            String str=txt.getText();
            if(str.length()==0) {
                M_keyTyped.invoke(scr, ' ', 14);
            }else{
                String str2=txt.getText();
                str2=str2.substring(0,str2.length()-1);
                wrapper.setTextNoEvent(str2);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onTabComplete(JTextField txt) {

    }

    @Override
    public boolean sameAs(GuiScreen screen, GuiTextField txtField) {
        return scr==screen;
    }*/
}
