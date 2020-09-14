package club.sk1er.patcher.tweaker.asm;

import club.sk1er.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class WorldRendererTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.renderer.WorldRenderer"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        final String owner = "net/minecraft/client/renderer/WorldRenderer";

        outer:
        for (MethodNode method : classNode.methods) {
            final String methodName = mapMethodName(classNode, method);
            if (methodName.equalsIgnoreCase("finishDrawing") || methodName.equalsIgnoreCase("func_178977_d")) {
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        if (((MethodInsnNode) next).name.equalsIgnoreCase("limit") && ((MethodInsnNode) next).owner.equalsIgnoreCase("java/nio/ByteBuffer")) {
                            final InsnList insns = new InsnList();
                            insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            insns.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "field_178999_b", "Ljava/nio/IntBuffer;"));
                            insns.add(new InsnNode(Opcodes.ICONST_0));
                            insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/nio/IntBuffer", "position", "(I)Ljava/nio/Buffer;", false));
                            method.instructions.insert(next.getNext(), insns);
                            continue outer;
                        }
                    }
                }
            } else if (methodName.equalsIgnoreCase("endVertex") || methodName.equalsIgnoreCase("func_181675_d")) {
                final InsnList insns = new InsnList();
                insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insns.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "field_178999_b", "Ljava/nio/IntBuffer;"));
                insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insns.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "field_178999_b", "Ljava/nio/IntBuffer;"));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/nio/IntBuffer", "position", "()I", false));
                insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insns.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "field_179011_q", "Lnet/minecraft/client/renderer/vertex/VertexFormat;"));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/vertex/VertexFormat", "func_181719_f", "()I", false));
                insns.add(new InsnNode(Opcodes.IADD));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/nio/IntBuffer", "position", "(I)Ljava/nio/Buffer;", false));
                insns.add(new InsnNode(Opcodes.POP));
                method.instructions.insertBefore(method.instructions.getFirst(), insns);
            }
        }
    }
}