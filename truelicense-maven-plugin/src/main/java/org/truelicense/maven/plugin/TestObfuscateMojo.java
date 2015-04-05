/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @deprecated Use the {@code obfuscate-test-classes} goal instead.
 * @author Christian Schlichtherle
 */
@Deprecated
@Mojo(name = "test-obfuscate", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES)
public final class TestObfuscateMojo extends ObfuscateTestClassesMojo { }
