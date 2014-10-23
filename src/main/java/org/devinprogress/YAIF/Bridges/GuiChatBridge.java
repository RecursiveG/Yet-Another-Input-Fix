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

/**
 * Created by recursiveg on 14-9-13.
 */
public class GuiChatBridge implements IActionBridge {
    private GuiChat screen=null;
    private GuiTextField txt=null;
    private InputFieldWrapper wrapper=null;

    private boolean isCmd=false;
    //private static Method keyTypedMethod=null;

    public GuiChatBridge(GuiTextField textField,GuiChat screen,InputFieldWrapper wrapper){
        this.screen=screen;
        txt=textField;
        this.wrapper=wrapper;
        wrapper.DoActions(ActionFeedback.SetText,txt.getText());

        if (screen.defaultInputFieldText.equals("/"))
            isCmd=true;
        else
            isCmd=false;
    }

    @Override
    public ActionFeedback onEnter(JTextField txt) { //send
        this.txt.setText(txt.getText());
        screen.keyTyped('\n', 28);//Magic Numbers can be found at http://minecraft.gamepedia.com/Key_Codes
        Keyboard.enableRepeatEvents(false);
        FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().resetScroll();

        return ActionFeedback.Quit;
    }

    @Override
    public ActionFeedback onEsc(JTextField txt) {
        //SetInGameFocus will close the GuiChat.
        Keyboard.enableRepeatEvents(false);
        FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().resetScroll();
        return ActionFeedback.Quit;
    }

    @Override
    public ActionFeedback onChange(final JTextField txt) {
        final String str;
        if(txt.getText().length()>100){
            str=txt.getText().substring(0,100);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    txt.setText(str);
                }
            });
        }else{
            str=txt.getText();
        }
        this.txt.setText(str);
        return IActionBridge.ActionFeedback.Nothing;
    }

    @Override
    public ActionFeedback onTab(JTextField txt) {
        //You have to listen to S3APacketTabComplete to get the compliance result.
        //YetAnotherInputFix.logger.info("Tab Completion not finished yet.");
        int cursorPos=txt.getCaretPosition();
        this.txt.setCursorPosition(cursorPos);
        screen.keyTyped('\t',15);
        wrapper.setTextNoEvent(this.txt.getText());
        //TODO: Finish it.
        return null;//return null == return Nothing
    }

    @Override
    public ActionFeedback onUp(JTextField txt) {
        screen.keyTyped( ' ', 200);
        wrapper.setTextNoEvent(this.txt.getText());
        return null;
    }

    @Override
    public ActionFeedback onDown(JTextField txt) {
        screen.keyTyped(' ', 208);
        wrapper.setTextNoEvent(this.txt.getText());
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
    public void onTabComplete(JTextField txt) {
        wrapper.setTextNoEvent(this.txt.getText());
    }

    @Override
    public boolean sameAs(GuiScreen screen, GuiTextField txtField) {
        return this.screen==screen && txtField==txt;
    }

    public boolean isCommand(){
        return isCmd;
    }
}
