package org.devinprogress.yaif;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import org.devinprogress.yaif.bridges.BaseActionBridge;
import org.devinprogress.yaif.bridges.DoNothingBridge;
import org.devinprogress.yaif.bridges.EditSignBridge;
import org.devinprogress.yaif.bridges.GuiChatBridge;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

/* NEVER try to figure out how this f**king machine works */
public class GuiStateManager {
    private static GuiStateManager INSTANCE = null;
    private InputFieldWrapper wrapper = null;
    private GuiScreen currentScreen = null, incomingScreen = null;
    private GuiTextField currentTextField = null;
    private BaseActionBridge bridge = null;
    private Set<Class> InputableGui = new HashSet<Class>() {{
        add(GuiEditSign.class);
        add(GuiScreenBook.class);
    }};
    private Set<Class> UnInputableGui = new HashSet<Class>();

    private GuiStateManager() {
        if (INSTANCE != null)
            throw new RuntimeException("Duplicated Initialization for GuiStateManager");
    }

    public static GuiStateManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GuiStateManager();
        return INSTANCE;
    }

    public void setWrapper(InputFieldWrapper w) {
        if (wrapper != null)
            throw new RuntimeException("InputFieldWrapper already set.");
        wrapper = w;
    }

    public void TextFieldFocusChanged(GuiScreen screen, GuiTextField textField, boolean isFocused) {
        if (isFocused) {
            if (screen == currentScreen) {//textField switched in the same GUI
                wrapper.releaseCurrentBridge();
                currentTextField = textField;
                bridge = getNewBridge();
                wrapper.setupBridge(bridge);
            } else {//the TextField in a new bridge
                if (screen == incomingScreen) {
                    currentScreen = screen;
                    incomingScreen = null;
                    currentTextField = textField;
                    bridge = getNewBridge();
                    wrapper.setupBridge(bridge);
                }/*else{
                    YetAnotherInputFix.log("WTF TextField %s Init without screen?",textField);
                }*/
            }
        } else {
            if (textField == currentTextField) {
                wrapper.releaseCurrentBridge();
                bridge = null;
                currentTextField = null;
            }
            if (currentScreen instanceof GuiContainerCreative) {
                wrapper.closeInputField();
                bridge = null;
            }
        }
    }

    private BaseActionBridge getNewBridge() {
        if (currentScreen instanceof GuiChat)
            return new GuiChatBridge(currentTextField, (GuiChat) currentScreen, wrapper);
        else if (currentScreen instanceof GuiEditSign)
            return new EditSignBridge((GuiEditSign) currentScreen, wrapper);
        else
            return new DoNothingBridge();
    }

    public void onTabCompletePacket(GuiScreen screen) {
        GuiChat chatScreen;
        if (screen instanceof GuiChat)
            chatScreen = (GuiChat) screen;
        else
            throw new RuntimeException("PacketTabComplete Received but GuiChat was not shown.");
        bridge.onTabComplete(chatScreen);
    }

    public void preInitGuiEvent(GuiScreen gui) {
        if (hasGuiTextField(gui))
            incomingScreen = gui;
        else {
            incomingScreen = null;
            wrapper.closeInputField();
            bridge = null;
        }
    }

    public void postInitGuiEvent(GuiScreen screen) {
        if (screen instanceof GuiEditSign || screen instanceof GuiScreenBook) {
            currentScreen = screen;
            incomingScreen = null;
            bridge = getNewBridge();
            wrapper.setupBridge(bridge);
        }
        if (incomingScreen == screen) {
            currentScreen = incomingScreen;
            incomingScreen = null;
        } else if (bridge != null) {
            bridge.postGuiInit();
        }
    }

    public void nullGuiOpenEvent(GuiScreen currentScreen) {
        wrapper.closeInputField();
        if (currentScreen instanceof GuiEditSign || currentScreen instanceof GuiScreenBook)
            YetAnotherInputFix.needFocus = true;
        bridge = null;
        this.currentScreen = null;
        this.currentTextField = null;
        this.incomingScreen = null;
    }

    public void inputFieldClosed() {
        bridge = null;
        this.currentScreen = null;
        this.currentTextField = null;
        this.incomingScreen = null;
        YetAnotherInputFix.needFocus = true;
    }

    private boolean hasGuiTextField(GuiScreen gui) {
        if (gui == null) return false;
        Class GuiClass = gui.getClass();
        if (UnInputableGui.contains(GuiClass)) return false;
        if (InputableGui.contains(GuiClass)) return true;
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
