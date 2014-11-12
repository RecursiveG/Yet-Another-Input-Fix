package org.devinprogress.YAIF;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiEditSign;
import org.devinprogress.YAIF.Bridges.*;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by recursiveg on 14-11-11.
 */
public class GuiStateManager {
    private static GuiStateManager INSTANCE=null;
    private InputFieldWrapper wrapper=null;
    private GuiScreen currentScreen=null,incomingScreen=null;
    private GuiTextField currentTextField=null;
    private BaseActionBridge bridge=null;
    private Set<Class> InputableGui=new HashSet<Class>(){{add(GuiEditSign.class);add(GuiScreenBook.class);}};
    private Set<Class> UnInputableGui=new HashSet<Class>();

    private GuiStateManager(){
        if (INSTANCE!=null)
            throw new RuntimeException("Duplicated Initialization for GuiStateManager");
    }

    public static GuiStateManager getInstance(){
        if(INSTANCE==null)
            INSTANCE=new GuiStateManager();
        return INSTANCE;
    }

    public void setWrapper(InputFieldWrapper w){
        if(wrapper!=null)
            throw new RuntimeException("InputFieldWrapper already set.");
        wrapper=w;
    }

    public void TextFieldFocusChanged(GuiScreen screen, GuiTextField textField, boolean isFocused) {
        if(isFocused){
            if(screen==currentScreen){//textField switched in the same GUI
                wrapper.releaseCurrentBridge();
                currentTextField=textField;
                bridge=getNewBridge();
                wrapper.setupBridge(bridge);
            }else{//the TextField in a new bridge
                if(screen==incomingScreen) {
                    currentScreen = incomingScreen;
                    currentTextField = textField;
                    bridge = getNewBridge();
                    wrapper.setupBridge(bridge);/*
                }else if(screen instanceof GuiContainerCreative){
                    currentScreen = screen;
                    currentTextField = textField;
                    bridge = getNewBridge();
                    wrapper.setupBridge(bridge);*/
                }else{
                    YetAnotherInputFix.log("WTF TextField %s Init without screen?",textField);
                }
            }
        }else{
            if(textField==currentTextField) {
                wrapper.releaseCurrentBridge();
                bridge = null;
                currentTextField=null;
            }
        }
    }

    private BaseActionBridge getNewBridge() {
        if(currentScreen instanceof GuiChat)
            return new GuiChatBridge(currentTextField,(GuiChat)currentScreen,wrapper);
        else if(currentScreen instanceof GuiEditSign)
            return new EditSignBridge((GuiEditSign)currentScreen,wrapper);
        else if(hasGuiTextField(currentScreen))
            return new CommonBridgeTextField(currentScreen,currentTextField,wrapper);
        else
            return new CommonBridgeNoField(currentScreen,wrapper);
    }

    public void onTabCompletePacket(GuiScreen screen) {
        GuiChat chatScreen=null;
        if(screen instanceof GuiChat)
            chatScreen=(GuiChat)screen;
        else
            throw new RuntimeException("PacketTabComplete Received but GuiChat was not shown.");
        bridge.onTabComplete(chatScreen);
    }

    public void preInitGuiEvent(GuiScreen gui){
        if(hasGuiTextField(gui))
            incomingScreen=gui;
        else
            incomingScreen=null;
    }

    public void postInitGuiEvent(GuiScreen screen) {
        if(screen instanceof GuiEditSign||screen instanceof GuiScreenBook){
            currentScreen=screen;
            bridge=getNewBridge();
            wrapper.setupBridge(bridge);
        }
        if (incomingScreen!=null) {
            currentScreen = incomingScreen;
            incomingScreen = null;
        }else{
            wrapper.closeInputField();
            bridge=null;
            this.currentScreen=null;
            this.currentTextField=null;
            this.incomingScreen=null;
        }
    }

    public void nullGuiOpenEvent(GuiScreen currentScreen) {
        wrapper.closeInputField();
        if(currentScreen instanceof GuiEditSign)
            YetAnotherInputFix.needFocus=true;
        bridge=null;
        this.currentScreen=null;
        this.currentTextField=null;
        this.incomingScreen=null;
    }

    public void inputFieldClosed(){
        bridge=null;
        this.currentScreen=null;
        this.currentTextField=null;
        this.incomingScreen=null;
        YetAnotherInputFix.needFocus=true;
    }

    private boolean hasGuiTextField(GuiScreen gui){
        if(gui==null)return false;
        Class GuiClass=gui.getClass();
        if(UnInputableGui.contains(GuiClass))return false;
        if(InputableGui.contains(GuiClass))return true;
        boolean hasTextField = false;

        for (Field f : GuiClass.getDeclaredFields()) {
            if (f.getType() == GuiTextField.class) {
                hasTextField = true;
                break;
            }
        }

        if (hasTextField)
            InputableGui.add(GuiClass);
        else
            UnInputableGui.add(GuiClass);

        return hasTextField;
    }
}
