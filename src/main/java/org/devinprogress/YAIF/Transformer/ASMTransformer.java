package org.devinprogress.YAIF.Transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.devinprogress.YAIF.YetAnotherInputFix;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Created by recursiveg on 14-9-12.
 */
public class ASMTransformer implements IClassTransformer {
    private ASMHelper asm=null;
    private static String obfedClassName=null;

    public ASMTransformer(){
        asm=new ASMHelper(this);
        asm.hookMethod("net.minecraft.client.Minecraft",                    "func_71384_a",   "startGame",          "()V",                                                         "insertWrapperStartup");
        asm.hookMethod("net.minecraft.client.gui.GuiTextField",             "func_146195_b",  "setFocused",         "(Z)V",                                                        "hookGuiTextFocusChange");
        asm.hookMethod("net.minecraft.client.network.NetHandlerPlayClient", "func_147274_a",  "handleTabComplete",  "(Lnet/minecraft/network/play/server/S3APacketTabComplete;)V", "hookPostTabComplete");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        obfedClassName=name;
        return asm.transform(name,transformedName,bytes);
    }

    /* These Transformers are designed for Minecraft version 1.7.10 */
    public static void insertWrapperStartup(MethodNode mn){
        //Add TextField Wrapper
        //org.devinprogress.YAIF.YetAnotherInputFix.SetupTextFieldWrapper(this.displayWidth, this.displayHeight);
        //before ForgeHooksClient.createDisplay();
        //At method startGame() ()V

        //ALOAD 0
        //GETFIELD net/minecraft/client/Minecraft.displayWidth : I
        //ALOAD 0
        //GETFIELD net/minecraft/client/Minecraft.displayHeight : I
        //INVOKESTATIC org/devinprogress/YAIF/YetAnotherInputFix.SetupTextFieldWrapper (II)V

        //Be careful of the Exception Labels!!!
        //Insert before INVOKESTATIC net/minecraftforge/client/ForgeHooksClient.createDisplay ()V
        //10th INVOKESTATIC

        AbstractInsnNode n=ASMHelper.getNthInsnNode(mn,Opcodes.INVOKESTATIC,10);
        String WidthName=YetAnotherInputFix.ObfuscatedEnv?"d":"displayWidth";
        String HeightName=YetAnotherInputFix.ObfuscatedEnv?"e":"displayHeight";

        if(!((MethodInsnNode)n).desc.equals("()V")) {//Here must be something wrong!
            System.out.println(new String("tryTransformMinecraft() Error, landmark desc not match."));
            return;
        }
        mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
        mn.instructions.insertBefore(n,new FieldInsnNode(Opcodes.GETFIELD,obfedClassName.replace('.','/'),WidthName,"I"));
        mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
        mn.instructions.insertBefore(n,new FieldInsnNode(Opcodes.GETFIELD,obfedClassName.replace('.','/'),HeightName,"I"));
        mn.instructions.insertBefore(n,new MethodInsnNode(Opcodes.INVOKESTATIC,
                "org/devinprogress/YAIF/YetAnotherInputFix","SetupTextFieldWrapper","(II)V",false));
        // Codes are braced by existed TryCatchBlock
        mn.maxStack+=1;
    }

    public static void hookGuiTextFocusChange(MethodNode mn){
        //Hook to GuiTextField onFocusChange
        //org.devinprogress.YAIF.YetAnotherInputFix.TextFieldFocusChange(this, p_146195_1_);
        //At the beginning/ending of setFocused (Z)V

        //ALOAD 0
        //ILOAD 1
        //INVOKESTATIC org/devinprogress/YAIF/YetAnotherInputFix.TextFieldFocusChange
        //   (Lnet/minecraft/client/gui/GuiTextField;Z)V  for for version 1.7.2

        //Remember to use the obfuscated name for Lnet/minecraft/client/gui/GuiTextField;
        AbstractInsnNode n=mn.instructions.getFirst();
        mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
        mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ILOAD,1));
        mn.instructions.insertBefore(n,new MethodInsnNode(Opcodes.INVOKESTATIC,
                "org/devinprogress/YAIF/YetAnotherInputFix","TextFieldFocusChange",
                "(L"+obfedClassName.replace('.','/')+";Z)V",false));
    }

    public static void hookPostTabComplete(MethodNode mn){
        AbstractInsnNode n=ASMHelper.getNthInsnNode(mn,Opcodes.INVOKEVIRTUAL,2);
        ASMHelper.InsertInvokeStaticAfter(mn,n,"org.devinprogress.YAIF.YetAnotherInputFix",
                "onTabCompletePacket","()V");
    }
}
