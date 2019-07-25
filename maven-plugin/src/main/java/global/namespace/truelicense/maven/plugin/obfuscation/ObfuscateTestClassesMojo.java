/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.obfuscation;

import global.namespace.truelicense.obfuscate.ObfuscatedString;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Transforms the byte code of the test class files in order to obfuscate all
 * constant string values in scope.
 *
 * @see ObfuscatedString
 */
@Mojo(name = "obfuscate-test-classes", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES)
public class ObfuscateTestClassesMojo extends ObfuscateClassesMojo {

    @Parameter(property = "truelicense.obfuscate.test.outputDirectory", defaultValue = "${project.build.testOutputDirectory}", readonly = true)
    private File testOutputDirectory;

    @Override
    protected File outputDirectory() { return testOutputDirectory; }
}
