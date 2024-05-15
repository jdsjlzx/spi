package com.rock.spi.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CallableDump implements Opcodes {
    /**
     * @param className eg: cn/rock/spi/asm/MyCallable
     * @param typeName  eg: java/lang/String
     */
    public static byte[] generateCallable(String className, String typeName) throws Exception {
        className = getASMClassName(className);
        typeName = getASMClassName(typeName);
        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, "Ljava/lang/Object;Ljava/util/concurrent/Callable<L" + typeName + ";>;", "java/lang/Object", new String[]{"java/util/concurrent/Callable"});

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "call", "()L" + typeName + ";", null, new String[]{"java/lang/Exception"});
            methodVisitor.visitCode();
            methodVisitor.visitTypeInsn(NEW, typeName);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, typeName, "<init>", "()V", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(2, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "call", "()Ljava/lang/Object;", null, new String[]{"java/lang/Exception"});
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, className, "call", "()L" + typeName + ";", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    //a.b -> a/b
    private static String getASMClassName(String className) {
        return className.replace(".", "/");
    }
}
