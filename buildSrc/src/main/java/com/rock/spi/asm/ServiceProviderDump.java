package com.rock.spi.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;

public class ServiceProviderDump implements Opcodes {

    public static byte[] dump(Map<String, String> callMap) throws Exception {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, "cn/rock/spi/ServiceProvider", null, "java/lang/Object", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, "sProviders", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/Class<*>;Ljava/util/concurrent/Callable<*>;>;", null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC + ACC_SYNCHRONIZED, "register", "(Ljava/lang/Class;Ljava/util/concurrent/Callable;)V", "(Ljava/lang/Class<*>;Ljava/util/concurrent/Callable<*>;)V", null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, "cn/rock/spi/ServiceProvider", "sProviders", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC + ACC_SYNCHRONIZED, "newProvider", "(Ljava/lang/Class;)Ljava/lang/Object;", "<S:Ljava/lang/Object;>(Ljava/lang/Class<TS;>;)TS;", new String[]{"java/lang/Exception"});
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, "cn/rock/spi/ServiceProvider", "sProviders", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, "java/util/concurrent/Callable");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/Callable", "call", "()Ljava/lang/Object;", true);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "java/util/LinkedHashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false);
            mv.visitFieldInsn(PUTSTATIC, "cn/rock/spi/ServiceProvider", "sProviders", "Ljava/util/Map;");

            //注册Callable
            for (Map.Entry<String, String> entry : callMap.entrySet()) {
                String key = entry.getKey().replace(".", "/");
                String value = entry.getValue().replace(".", "/");
                mv.visitLdcInsn(Type.getType("L" + key + ";"));
                mv.visitTypeInsn(NEW, value);
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, value, "<init>", "()V", false);
                mv.visitMethodInsn(INVOKESTATIC, "cn/rock/spi/ServiceProvider", "register", "(Ljava/lang/Class;Ljava/util/concurrent/Callable;)V", false);
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 0);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}