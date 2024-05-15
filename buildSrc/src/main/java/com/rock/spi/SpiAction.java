package com.rock.spi;

import com.rock.spi.asm.CallableDump;
import com.rock.spi.asm.ServiceProviderDump;
import com.rock.spi.services.AnnotationFilter;

import org.gradle.api.file.FileCollection;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpiAction {
    private static final String packageName = "cn/rock/spi";
    private static final String PROVIDER = "ServiceProvider.class";
    private final File sourceDir;
    private final File servicesDir;
    private final FileCollection classPath;

    public SpiAction(File srcDir, File servicesDir, FileCollection classPath) {
        this.sourceDir = srcDir;
        this.servicesDir = servicesDir;
        this.classPath = classPath;
    }

    public boolean execute() {
        try {
            delete(new File(sourceDir, PROVIDER));
            delete(servicesDir);
            Map<String, String> service = getServices(classPath.getFiles());
            Map<String, String> callables = newCallables(service);
            byte[] bytes = ServiceProviderDump.dump(callables);
            writeBytes(sourceDir, PROVIDER, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private Map<String, String> getServices(Set<File> set) throws Exception {
        Map<String, String> services = null;
        for (File file : set) {
            services = AnnotationFilter.filterService(file);
            for (Map.Entry<String, String> entry : services.entrySet()) {
                String infName = entry.getKey();
                String implName = entry.getValue();

                final File spi = new File(servicesDir, infName);
                PrintWriter writer = new PrintWriter(new FileWriter(spi, true));
                writer.printf(implName).println();
                writer.flush();
                writer.close();
            }
        }
        return services;
    }

    /**
     * @return key=接口名，value=自定义Callable类名
     */
    private Map<String, String> newCallables(Map<String, String> services) throws Exception {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : services.entrySet()) {
            String implName = entry.getValue();
            String callableClassName = implName.substring(implName.lastIndexOf(".") + 1) + "Callable";
            byte[] bytes = CallableDump.generateCallable(packageName + "/" + callableClassName, implName);
            writeBytes(sourceDir, callableClassName + ".class", bytes);
            map.put(entry.getKey(), packageName + "/" + callableClassName);
        }
        return map;
    }

    private void writeBytes(File srcDir, String clzName, byte[] bytes) {
        File dir = new File(srcDir, SpiAction.packageName);
        if (dir.isDirectory() && !dir.exists()) {
            dir.mkdirs();
        }
        File clzFile = new File(dir, clzName);
        try (OutputStream out = new FileOutputStream(clzFile); BufferedOutputStream buff = new BufferedOutputStream(out)) {
            buff.write(bytes);
            buff.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delete(f);
            }
        } else {
            file.delete();
        }
    }
}
