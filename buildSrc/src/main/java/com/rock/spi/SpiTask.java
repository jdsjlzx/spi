package com.rock.spi;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class SpiTask extends DefaultTask {
    private File sourceDir;
    private File servicesDir;
    private FileCollection classPath;

    public SpiTask() {
        this.classPath = getProject().files();
    }

    public void setClassPath(FileCollection path) {
        this.classPath = path;
    }

    public void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    public void setServicesDir(File servicesDir) {
        this.servicesDir = servicesDir;
    }

    @InputFiles
    public FileCollection getClassPath() {
        return classPath;
    }

    @OutputDirectory
    public File getSourceDir() {
        return sourceDir;
    }

    @OutputDirectory
    public File getServicesDir() {
        return servicesDir;
    }

    @TaskAction
    protected void generate() {
        setDidWork(new SpiAction(sourceDir, servicesDir, classPath).execute());
    }
}