package org.devinprogress.yaif.fmlplugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Created by recursiveg on 15-2-21.
 */
public class BytecodeTransformer extends BaseAsmTransformer {
    public BytecodeTransformer(){
        super();
        hookMethod("net.minecraft.client.Minecraft", "func_71384_a", "startGame", "()V", new initWrapper());
        hookMethod("net.minecraft.client.gui.GuiTextField", "func_146195_b", "setFocused", "(Z)V", new focusHook());
        hookMethod("net.minecraft.client.network.NetHandlerPlayClient", "func_147274_a", "handleTabComplete",
                "(Lnet/minecraft/network/play/server/S3APacketTabComplete;)V", new onTabComplete());

    }

    private class initWrapper implements IMethodTransformer{
        @Override
        public void transform(MethodNode mn, String srgName, boolean devEnv, String obfedClassName) {
            AbstractInsnNode n=getNthInsnNode(mn, Opcodes.ALOAD,15);
            String widthVarName=devEnv?"displayWidth":"field_71443_c";
            String heightVarName=devEnv?"displayHeight":"field_71440_d";
            mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
            mn.instructions.insertBefore(n,new FieldInsnNode(Opcodes.GETFIELD,obfedClassName.replace('.','/'),widthVarName,"I"));
            mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
            mn.instructions.insertBefore(n,new FieldInsnNode(Opcodes.GETFIELD,obfedClassName.replace('.','/'),heightVarName,"I"));
            mn.instructions.insertBefore(n,new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "org/devinprogress/yaif/YetAnotherInputFix","SetupTextFieldWrapper","(II)V",false));
        }
    }

    private class focusHook implements IMethodTransformer{
        @Override
        public void transform(MethodNode mn, String srgName, boolean devEnv, String obfedClassName) {
            AbstractInsnNode n=mn.instructions.getFirst();
            mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
            mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ILOAD,1));
            mn.instructions.insertBefore(n,new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "org/devinprogress/yaif/YetAnotherInputFix","TextFieldFocusChange",
                    "(L"+obfedClassName.replace('.','/')+";Z)V",false));
        }
    }

    private class onTabComplete implements IMethodTransformer{
        @Override
        public void transform(MethodNode mn, String srgName, boolean devEnv, String classObfName) {
            AbstractInsnNode n=getNthInsnNode(mn, Opcodes.INVOKEVIRTUAL, 2);
            mn.instructions.insert(n, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "org/devinprogress/yaif/YetAnotherInputFix", "onTabCompletePacket", "()V", false));
        }
    }
}
