package org.devinprogress.yaif;

import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

@Mod(modid = "yaif")
public class YetAnotherInputFix {
    private static GuiStateManager stateMachine = null;
    public static boolean ObfuscatedEnv = true;

    public static void log(String msg, Object... args) {
        LogManager.getLogger("YAIF").info(String.format(msg, args));
    }

    //Will be called before the Constructor! Be careful.
    public static void SetupTextFieldWrapper(int W, int H) {
        log("Now setting Wrapper {Width:%d, Hight:%d}", W, H);
        stateMachine = GuiStateManager.getInstance();
        stateMachine.setWrapper(new InputFieldWrapper(W, H));
    }

    //Called from GuiTextField.setFocused() due to ASMTransformed
    public static void TextFieldFocusChange(GuiTextField textField, boolean isFocused) {
        //log("TextField State Changed {textField:%s, focused:%s}",textField.toString(),isFocused);
        stateMachine.TextFieldFocusChanged(FMLClientHandler.instance().getClient().currentScreen, textField, isFocused);
    }

    //called from net.minecraft.client.network.NetHandlerPlayClient.handleTabComplete
    public static void onTabCompletePacket() {
        //log("TabComplete Packet Received");
        stateMachine.onTabCompletePacket(FMLClientHandler.instance().getClient().currentScreen);
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        //log("FMLMod Initialization");
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onGuiChange(GuiScreenEvent.InitGuiEvent.Post e) {
        //log("PostGuiChangeEvent {GUI:%s}",e.gui.toString());
        stateMachine.postInitGuiEvent(e.getGui());
    }

    @SubscribeEvent
    public void preGuiInit(GuiScreenEvent.InitGuiEvent.Pre e) {
        //log("PreGuiInitEvent {GUI:%s}",e.gui.toString());
        stateMachine.preInitGuiEvent(e.getGui());
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent e) {
        if (e.getGui() == null) {
            //log("NullGui Open");
            stateMachine.nullGuiOpenEvent(FMLClientHandler.instance().getClient().currentScreen);
        }
    }

    //Multi-threading is a problem
    //TODO: UGLY PATCH!!!
    //TODO: Use better ways to deal with the problems addressed below
    public static boolean needFocus = false;
    public static boolean needCurrent = false;
    private static int downCounter = 0;
    private static int logCounter = 0;

    @SubscribeEvent
    public void tryGetFocus(TickEvent.ClientTickEvent event) {
        if (needFocus) {
            try {
                Display.makeCurrent();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ++logCounter;
            if (Display.isActive()) {
                FMLClientHandler.instance().getClient().setIngameFocus();
                //log("Focus Grabbed after %s tries",logCounter);
                needFocus = false;
                logCounter = 0;
            }
        }
        if (needCurrent) {
            downCounter = 100;
            needCurrent = false;
        }
        if (--downCounter > 0) {
            try {
                Display.makeCurrent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
