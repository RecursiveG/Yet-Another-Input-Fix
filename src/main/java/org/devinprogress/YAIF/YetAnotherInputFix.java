package org.devinprogress.YAIF;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by recursiveg on 14-9-10.
 */

@Mod(modid="YAIF", name="YetAnotherInputFix", version="1.7.2", dependencies="required-after:FML")
public class YetAnotherInputFix{

    public static boolean ObfuscatedEnv=true;
    private static Set<Class<?>> InputableGui = new HashSet<Class<?>>();
    private static Set<Class<?>> UnInputableGui = new HashSet<Class<?>>();
    public static GuiScreen currentGuiScreen = null;
    public static GuiTextField currentTextField = null;
    private static InputFieldWrapper wrapper =null;
    public static GuiTextField txt = null;
    public static final Logger logger=Logger.getLogger("YAIF");
    public static boolean needFocus=false;
    private KeyBinding debugKey=new KeyBinding("Debug Key",Keyboard.KEY_Y,"YAIF");

    //Will be called before the Constructor! Be careful.
    public static void SetupTextFieldWrapper(int W, int H){
        wrapper=new InputFieldWrapper(W,H);
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(debugKey);
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent e){
        if(debugKey.isPressed()){
            wrapper.hide();
        }
    }

    @SubscribeEvent
    public void onGuiChange(GuiScreenEvent.InitGuiEvent.Post e) {
        if(e.gui instanceof GuiEditSign){
            currentGuiScreen=e.gui;
            currentTextField=null;
            wrapper.show();
        }
        if(e.gui instanceof GuiScreenBook){
            currentGuiScreen=e.gui;
            currentTextField=null;
            wrapper.show();
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

    @SubscribeEvent
    public void onGuiClosing(GuiOpenEvent e){
        if(e.gui==null&&FMLClientHandler.instance().getClient().currentScreen==currentGuiScreen&&wrapper!=null)
            wrapper.hide();
    }

    //Called from GuiTextField.setFocused() due to ASMTransformed
    public static void TextFieldFocusChange(GuiTextField textField, boolean isFocused) {
        if (isFocused) {
            GuiScreen sc= FMLClientHandler.instance().getClient().currentScreen;
            if(GuiCanInput(sc)){
                currentGuiScreen=sc;
                currentTextField=textField;
                wrapper.show();
            }
        } else {
            if (currentTextField == textField) {
                currentTextField = null;
                wrapper.hide();
            }
        }
    }

    @SubscribeEvent
    public void keyEvents(InputEvent.KeyInputEvent e){
        System.out.println(String.format("Typedkey=%d",Keyboard.getEventKey()));
    }

    private static boolean GuiCanInput(GuiScreen gui){
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