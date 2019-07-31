/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.obfuscation;

import global.namespace.truelicense.obfuscate.ObfuscatedString;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Path;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;

/**
 * Transforms the byte code of the main class files in order to obfuscate all
 * constant string values in scope.
 *
 * @see ObfuscatedString
 */
@Mojo(name = "obfuscate-main-classes", defaultPhase = PROCESS_CLASSES)
public final class ObfuscateMainClassesMojo extends ObfuscateClassesMojo {

    @Parameter(property = "truelicense.obfuscate.main.outputDirectory", defaultValue = "${project.build.outputDirectory}", readonly = true)
    private File outputDirectory;

    /**
     * This dependency provider method is used to wire
     * {@link global.namespace.truelicense.build.tasks.obfuscation.ObfuscateClassesTask}.
     *
     * @see #task()
     */
    Path outputDirectory() {
        return outputDirectory.toPath();
    }
}
