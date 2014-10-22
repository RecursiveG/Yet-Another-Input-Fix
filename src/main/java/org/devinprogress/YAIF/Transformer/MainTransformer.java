package org.devinprogress.YAIF;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Created by recursiveg on 14-9-12.
 */
public class Transformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(transformedName.equalsIgnoreCase("net.minecraft.client.Minecraft"))
            return tryTransformMinecraft(bytes, !name.equals(transformedName),name);
        if(transformedName.equalsIgnoreCase("net.minecraft.client.gui.GuiTextField"))
            return tryTransformGuiTextField(bytes, !name.equals(transformedName),name);
        return bytes;
    }
    /* This Transformer is designed for Minecraft version 1.7.2 */
    private byte[] tryTransformMinecraft(byte[] orig,boolean obf,String obfedClassName){
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
        //11th INVOKESTATIC

        String TargetName=obf?"Z":"startGame";
        String WidthName=obf?"d":"displayWidth";
        String HeightName=obf?"e":"displayHeight";

        String TargetDesc="()V";
        ClassReader cr=new ClassReader(orig);
        ClassNode cn=new ClassNode();
        cr.accept(cn, 0);


        for(MethodNode mn:cn.methods){
            if(mn.name.equals(TargetName)&&mn.desc.equals(TargetDesc)){
                AbstractInsnNode n=mn.instructions.getFirst();
                int i=0;
                while(i<11){
                    if(n.getOpcode()==Opcodes.INVOKESTATIC)
                        i++;
                    n=n.getNext();
                }
                //We are now at the next line!
                n=n.getPrevious();
                if(!((MethodInsnNode)n).desc.equals("()V")) {//Here must be something wrong!
                    System.out.println(new String("tryTransformMinecraft() Error, landmark desc not match."));
                    return orig;
                }
                mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
                mn.instructions.insertBefore(n,new FieldInsnNode(Opcodes.GETFIELD,obfedClassName.replace('.','/'),WidthName,"I"));
                mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
                mn.instructions.insertBefore(n,new FieldInsnNode(Opcodes.GETFIELD,obfedClassName.replace('.','/'),HeightName,"I"));
                mn.instructions.insertBefore(n,new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "org/devinprogress/YAIF/YetAnotherInputFix","SetupTextFieldWrapper","(II)V"));
                // Codes are braced by existed TryCatchBlock
                mn.maxStack+=1;
                break;
            }
        }

        ClassWriter cw=new ClassWriter(0);//AutoCompute will crash
        cn.accept(cw);
        return cw.toByteArray();

        //return bytes;
    }
    private byte[] tryTransformGuiTextField(byte[] orig,boolean obf,String obfedClassName){
        //Hook to GuiTextField onFocusChange
        //org.devinprogress.YAIF.YetAnotherInputFix.TextFieldFocusChange(this, p_146195_1_);
        //At the beginning/ending of setFocused (Z)V

        //ALOAD 0
        //ILOAD 1
        //INVOKESTATIC org/devinprogress/YAIF/YetAnotherInputFix.TextFieldFocusChange
        //   (Lnet/minecraft/client/gui/GuiTextField;Z)V  for for version 1.7.2

        //Remember to use the obfuscated name for Lnet/minecraft/client/gui/GuiTextField;

        String TargetName=obf?"b":"setFocused";
        String TargetDesc="(Z)V";
        ClassReader cr=new ClassReader(orig);
        ClassNode cn=new ClassNode();
        cr.accept(cn, 0);


        for(MethodNode mn:cn.methods){
            if(mn.name.equals(TargetName)&&mn.desc.equals(TargetDesc)){
                AbstractInsnNode n=mn.instructions.getFirst();
                mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ALOAD,0));
                mn.instructions.insertBefore(n,new VarInsnNode(Opcodes.ILOAD,1));
                mn.instructions.insertBefore(n,new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "org/devinprogress/YAIF/YetAnotherInputFix","TextFieldFocusChange",
                        "(L"+obfedClassName.replace('.','/')+";Z)V"));
                break;
            }
        }

        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
