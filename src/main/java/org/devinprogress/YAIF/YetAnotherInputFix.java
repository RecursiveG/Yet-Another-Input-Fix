package org.devinprogress.YAIF;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;

/**
 * Created by recursiveg on 14-9-10.
 */

@Mod(modid="YAIF", name="YetAnotherInputFix", version="0.2-dev", dependencies="required-after:FML")
public class YetAnotherInputFix{
    private static GuiStateManager stateMachine=null;
    public static boolean ObfuscatedEnv=true;
    public static boolean needFocus=false;

    public static void log(String msg,Object... args){
        LogManager.getLogger("YAIF").info(String.format(msg,args));
    }
    //Will be called before the Constructor! Be careful.
    public static void SetupTextFieldWrapper(int W, int H){
        log("Now setting Wrapper {Width:%d, Hight:%d}",W,H);
        stateMachine=GuiStateManager.getInstance();
        stateMachine.setWrapper(new InputFieldWrapper(W,H));
    }

    //Called from GuiTextField.setFocused() due to ASMTransformed
    public static void TextFieldFocusChange(GuiTextField textField, boolean isFocused) {
        log("TextField State Changed {textField:%s, focused:%s}",textField.toString(),isFocused);
        stateMachine.TextFieldFocusChanged(FMLClientHandler.instance().getClient().currentScreen,textField,isFocused);
    }

    //called from net.minecraft.client.network.NetHandlerPlayClient.handleTabComplete
    public static void onTabComplete(){
        log("TabComplete Packet Received");
        stateMachine.onTabCompletePacket(FMLClientHandler.instance().getClient().currentScreen);
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        log("FMLMod Initialization");
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onGuiChange(GuiScreenEvent.InitGuiEvent.Post e) {
        log("PostGuiChangeEvent {GUI:%s}",e.gui.toString());
        stateMachine.postInitGuiEvent(e.gui);
    }

    @SubscribeEvent
    public void preGuiInit(GuiScreenEvent.InitGuiEvent.Pre e){
        log("PreGuiInitEvent {GUI:%s}",e.gui.toString());
        stateMachine.preInitGuiEvent(e.gui);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent e){
        if(e.gui==null) {
            log("NullGui Open");
            stateMachine.nullGuiOpenEvent(FMLClientHandler.instance().getClient().currentScreen);
        }
    }

    //Multi-threading is a problem
    //TODO: UGLY PATCH!!!
    //TODO: use ASM to reduce potential lag
    @SubscribeEvent
    public void tryGetFocus(TickEvent.ClientTickEvent event){
        if(needFocus){
            try{
                Display.makeCurrent();
            }catch(Exception e){
                e.printStackTrace();
            }
            if(Display.isActive()){
                FMLClientHandler.instance().getClient().setIngameFocus();

                needFocus=false;
            }
        }
    }
}