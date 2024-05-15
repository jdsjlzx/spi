package com.rock.spi.services;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cn.rock.spi.ServiceProviderInterface;

public class AnnotationFilter {
    public static Map<String, String> filterService(File file) throws Exception {
        Map<String, String> map = new HashMap<>();
        final Stack<File> stack = new Stack<>();
        stack.push(file);

        while (!stack.isEmpty()) {
            final File f = stack.pop();
            if (f.isDirectory()) {
                final File[] files = f.listFiles();
                if (files != null) {
                    for (File child : files) {
                        stack.push(child);
                    }
                }
            } else if (f.getName().endsWith(".class")) {
                Map<String, String> m = parseClass(f);
                if (m != null) {
                    map.putAll(m);
                }
            } else if (f.getName().endsWith(".jar")) {
                Map<String, String> m = parseJar(new JarFile(f));
                if (m != null) {
                    map.putAll(m);
                }
            }
        }

        return map;
    }

    private static Map<String, String> parseClass(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        try {
            return parseAnnotatedClass(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static Map<String, String> parseJar(JarFile jar) throws IOException {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                InputStream inputStream = jar.getInputStream(jarEntry);
                try {
                    return parseAnnotatedClass(inputStream);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }
        return null;
    }

    private static Map<String, String> parseAnnotatedClass(InputStream inputStream) throws IOException {
        Map<String, String> map = new HashMap<>();
        ClassNode classNode = new ClassNode();
        new ClassReader(inputStream).accept(classNode, ClassReader.SKIP_DEBUG);
        List<AnnotationNode> annotations = classNode.visibleAnnotations;
        if (annotations == null) {
            return map;
        }

        String clzName = "L" + getASMClzName(ServiceProviderInterface.class) + ";";
        for (AnnotationNode node : annotations) {
            List<Object> values = node.values;
            if (values == null || !(clzName.equals(node.desc))) {
                continue;
            }
            for (Object v : values) {
                if (!(v instanceof List)) {
                    continue;
                }
                Type type = ((List<Type>) v).get(0);
                String infName = type.getClassName();
                map.put(infName, getJavaCLzName(classNode.name));
            }
        }

        return map;
    }

    private static String getASMClzName(Class clazz) {
        return clazz.getName().replace(".", "/");
    }

    private static String getJavaCLzName(String str) {
        return str.replace("/", ".");
    }
}
