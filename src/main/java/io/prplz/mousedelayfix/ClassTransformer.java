package io.prplz.mousedelayfix;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
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
                if (FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc).equals("func_70676_i")) {
                    System.out.println("Found getLook: " + method.name);
                    String entity = FMLDeobfuscatingRemapper.INSTANCE.unmap("net/minecraft/entity/Entity");
                    System.out.println("Found Entity: " + entity);
                    method.instructions.clear();
                    method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    method.instructions.add(new VarInsnNode(Opcodes.FLOAD, 1));
                    method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, entity, method.name, method.desc)); // super.getLook(f);
                    method.instructions.add(new InsnNode(Opcodes.ARETURN));
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
