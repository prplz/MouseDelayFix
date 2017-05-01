package io.prplz.mousedelayfix;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.entity.EntityLivingBase")) {
            System.out.println("Found EntityLivingBase: " + name);
            ClassReader classReader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            for (MethodNode method : classNode.methods) {
                String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc);
                if (mappedMethodName.equals("func_70676_i") || mappedMethodName.equals("getLook")) {
                    System.out.println("Found getLook: " + method.name);
                    String entity = FMLDeobfuscatingRemapper.INSTANCE.unmap("net/minecraft/entity/Entity");
                    System.out.println("Found Entity: " + entity);
                    String entityPlayerSP = FMLDeobfuscatingRemapper.INSTANCE.unmap("net/minecraft/client/entity/EntityPlayerSP");
                    System.out.println("Found EntityPlayerSP: " + entityPlayerSP);
                    /*
                    if (this instanceof EntityPlayerSP) {
                        return super.getLook(f);
                    }
                     */
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new TypeInsnNode(Opcodes.INSTANCEOF, entityPlayerSP));
                    LabelNode label = new LabelNode();
                    insnList.add(new JumpInsnNode(Opcodes.IFEQ, label));
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new VarInsnNode(Opcodes.FLOAD, 1));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, entity, method.name, method.desc));
                    insnList.add(new InsnNode(Opcodes.ARETURN));
                    insnList.add(label);
                    method.instructions.insertBefore(method.instructions.getFirst(), insnList);
                    break;
                }
            }
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return bytes;
    }
}
