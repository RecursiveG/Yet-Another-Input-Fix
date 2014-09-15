package org.devinprogress.YAIF;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.lang.reflect.Field;
import java.security.cert.Certificate;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by recursiveg on 14-9-10.
 */

@Mod(modid="YAIF", name="YetAnotherInputFix", version="1.7.2", dependencies="required-after:FML")
public class YetAnotherInputFix{

    public static boolean ObfuscatedEnv=true;
    private Set<Class<?>> InputableGui = new HashSet<Class<?>>();
    private Set<Class<?>> UnInputableGui = new HashSet<Class<?>>();
    public static GuiScreen currentGuiScreen = null;
    public static GuiTextField currentTextField = null;
    private static InputFieldWrapper wrapper =null;
    public static GuiTextField txt = null;
    public static final Logger logger=Logger.getLogger("YAIF");

    //Will be called before the Constructor! Be careful.
    public static void SetupTextFieldWrapper(int W, int H){
        wrapper=new InputFieldWrapper(W,H);
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onGuiChange(GuiOpenEvent e) {

        boolean hasTextField;
        if (e.gui != null) {
            Class GuiClass = e.gui.getClass();

            if (UnInputableGui.contains(GuiClass)) {
                hasTextField = false;
            } else if (InputableGui.contains(GuiClass)) {
                hasTextField = true;
            } else { //Use Reflection to Check all fields
                hasTextField = false;
                for (Field f : GuiClass.getDeclaredFields()) {
                    if (f.getType() == GuiTextField.class) {
                        hasTextField = true;
                        break;
                    }
                }
                /*TODO: Uncomment when bridges done
                //TODO: Find a better way to do this
                if( GuiClass.equals(GuiEditSign.class)    ||
                    GuiClass.equals(GuiScreenBook.class))
                    hasTextField=true;*/

                if (hasTextField)
                    InputableGui.add(GuiClass);
                else
                    UnInputableGui.add(GuiClass);
            }
        } else {
            hasTextField = false;
        }

        if (hasTextField) {
            currentGuiScreen = e.gui;
            currentTextField=null;
        } else {
            currentGuiScreen = null;
            wrapper.hide();
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
}