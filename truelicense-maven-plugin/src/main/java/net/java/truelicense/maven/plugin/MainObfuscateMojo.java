/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @deprecated Use the {@code obfuscate-main-classes} goal instead.
 * @author Christian Schlichtherle
 */
@Deprecated
@Mojo(name = "obfuscate", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class MainObfuscateMojo extends ObfuscateMainClassesMojo { }
