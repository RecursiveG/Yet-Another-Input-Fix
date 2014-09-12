package org.devinprogress.inputfix;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by recursiveg on 14-9-10.
 */

@Mod(modid="inputfix", name="InputFix", version="1.0.0")
public class Main {
    private Set<Class<?>> InputableGui = new HashSet<Class<?>>();
    private Set<Class<?>> UnInputableGui = new HashSet<Class<?>>();
    public static GuiScreen currentGuiScreen = null;
    public static GuiTextField currentTextField = null;
    private static InputFieldWrapper wrapper =null;
    public static GuiTextField txt = null;

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postLoad(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public void onGuiChange(GuiOpenEvent e) {

        boolean inputable;
        if (e.gui != null) {
            Class GuiClass = e.gui.getClass();
            if (UnInputableGui.contains(GuiClass)) {
                inputable = false;
            } else if (InputableGui.contains(GuiClass)) {
                inputable = true;
            } else { //Use Reflection to Check all fields
                inputable = false;
                for (Field f : GuiClass.getDeclaredFields()) {
                    if (f.getType() == GuiTextField.class) {
                        inputable = true;
                        break;
                    }
                }
                if (inputable)
                    InputableGui.add(GuiClass);
                else
                    UnInputableGui.add(GuiClass);
            }
        } else {
            inputable = false;
        }

        if (inputable) {
            currentGuiScreen = e.gui;
            //wrapper.show();
            //This Gui has GuiTextField
        } else {
            currentGuiScreen = null;
            wrapper.hide();
            //No GuiTextField TuT
        }
        currentTextField = null;
    }

    public static void TextFieldFocusChange(GuiTextField textField, boolean isFocused) {
        if (isFocused) {
            if (currentGuiScreen != null) {
                currentTextField = textField;
                wrapper.show();
            }else {
                currentTextField = null;
                wrapper.hide();
            }
        } else {
            if (currentTextField == textField) {
                currentTextField = null;
                wrapper.hide();
            }
        }
    }

    public static void SetupTextFieldWrapper(int W, int H){
        wrapper=new InputFieldWrapper(W,H);
    }
}