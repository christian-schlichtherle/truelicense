/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Transforms the byte code of the test class files in order to obfuscate all
 * constant string values in scope.
 *
 * @see org.truelicense.obfuscate.ObfuscatedString
 * @since TrueLicense 2.4
 * @author Christian Schlichtherle
 */
@Mojo(name = "obfuscate-test-classes", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES)
public class ObfuscateTestClassesMojo extends ObfuscateClassesMojo {

    @Parameter(defaultValue = "${project.build.testOutputDirectory}", readonly = true)
    private File testOutputDirectory;

    @Override
    protected File outputDirectory() { return testOutputDirectory; }
}
