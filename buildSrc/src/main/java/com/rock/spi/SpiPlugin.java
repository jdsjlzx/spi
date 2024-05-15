package com.rock.spi;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.specs.Spec;

import java.io.File;

public class SpiPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project p) {
                boolean isApp = p.getPlugins().hasPlugin("com.android.application");
                if (!isApp) {
                    return;
                }

                AppExtension android = p.getExtensions().findByType(AppExtension.class);
                for (ApplicationVariant variant : android.getApplicationVariants()) {
                    final File spiRoot = p.file(getSpiRootPath(p, variant));
                    final File dstDir = variant.getJavaCompile().getDestinationDir();
                    final FileCollection classPath = p.files(android.getBootClasspath(), variant.getJavaCompile().getClasspath(), dstDir);
                    SpiTask spiTask = p.getTasks().create(newTaskName(variant), SpiTask.class, new Action<SpiTask>() {
                        @Override
                        public void execute(SpiTask task) {
                            task.setClassPath(task.getClassPath().plus(classPath));
                            task.setSourceDir(dstDir);
                            task.setServicesDir(spiRoot);
                            task.getOutputs().upToDateWhen(new Spec<Task>() {
                                @Override
                                public boolean isSatisfiedBy(Task task) {
                                    return false;
                                }
                            });
                        }
                    });
                    spiTask.mustRunAfter(variant.getJavaCompile());
                    variant.getAssemble().dependsOn(spiTask);
                    if (variant.getInstall() != null) {
                        variant.getInstall().dependsOn(spiTask);
                    }
                }
            }
        });
    }

    private String getSpiRootPath(Project project, ApplicationVariant variant) {
        return project.getBuildDir() + "/intermediates/spi/" + variant.getDirName() + "/services";
    }

    private String newTaskName(ApplicationVariant variant) {
        return "generateServiceRegistry" + variant.getName();
    }
}