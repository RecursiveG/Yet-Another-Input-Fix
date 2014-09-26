package org.devinprogress.YAIF.Bridges;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.devinprogress.YAIF.InputFieldWrapper;
import org.devinprogress.YAIF.YetAnotherInputFix;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by recursiveg on 14-9-13.
 */
public class GuiChatBridge implements IActionBridge {
    private GuiChat screen=null;
    private GuiTextField txt=null;
    private InputFieldWrapper wrapper=null;

    private boolean isCmd=false;
    private static Method keyTypedMethod=null;

    public GuiChatBridge(GuiTextField textField,GuiChat screen,InputFieldWrapper wrapper){
        this.screen=screen;
        txt=textField;
        this.wrapper=wrapper;
        wrapper.DoActions(ActionFeedback.SetText,txt.getText());

        //TODO: use AccessTransformer instead of reflection
        if(keyTypedMethod==null){
            for(Method m:screen.getClass().getDeclaredMethods()){
                if(m.getParameterTypes().length==2&&m.getReturnType()==void.class&&m.getParameterTypes()[0]==char.class&&m.getParameterTypes()[1]==int.class){
                    //The Method Desc "(CI)V" seem to be unique
                    keyTypedMethod=m;
                    keyTypedMethod.setAccessible(true);
                }
            }
        }

        for(Field f:screen.getClass().getDeclaredFields()){
            if(f.getType().equals(String.class)){
                String def="";
                try {
                    f.setAccessible(true);
                    def=(String)(f.get(screen));
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(def.equals("/")){
                    isCmd=true;
                    break;
                }
            }
        }
    }

    @Override
    public ActionFeedback onEnter(JTextField txt) { //send
        this.txt.setText(txt.getText());
        try {
            keyTypedMethod.invoke(screen, '\n', 28);//Magic Numbers can be found at http://minecraft.gamepedia.com/Key_Codes
        }catch(Exception e){
            e.printStackTrace();
        }
        return ActionFeedback.Quit;
    }

    @Override
    public ActionFeedback onEsc(JTextField txt) {
        /*try {
            keyTypedMethod.invoke(screen, ' ', 1);
        }catch(Exception e){
            e.printStackTrace();
        }*/
        return ActionFeedback.Quit;
    }

    @Override
    public ActionFeedback onChange(JTextField txt) {
        this.txt.setText(txt.getText());
        return IActionBridge.ActionFeedback.Nothing;
    }

    @Override
    public ActionFeedback onTab(JTextField txt) {
        YetAnotherInputFix.logger.info("Tab Completion not finished yet.");
        //TODO: Finish it.
        return null;//return null == return Nothing
    }

    @Override
    public ActionFeedback onUp(JTextField txt) {
        try {
            keyTypedMethod.invoke(screen, ' ', 200);
        }catch(Exception e){
            e.printStackTrace();
        }
        wrapper.setTextNoEvent(this.txt.getText());
        return null;
    }

    @Override
    public ActionFeedback onDown(JTextField txt) {
        try {
            keyTypedMethod.invoke(screen, ' ', 208);
        }catch(Exception e){
            e.printStackTrace();
        }
        wrapper.setTextNoEvent(this.txt.getText());
        return null;
    }

    @Override
    public boolean sameAs(GuiScreen screen, GuiTextField txtField) {
        return this.screen==screen && txtField==txt;
    }

    public boolean isCommand(){
        return isCmd;
    }
}
