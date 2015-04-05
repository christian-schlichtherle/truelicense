/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin;

import org.truelicense.obfuscate.ObfuscatedString;

import javax.annotation.concurrent.Immutable;

/**
 * @author Christian Schlichtherle
 */
@Immutable
public final class JavaTool {

    public String obfuscatedString(String s) {
        return ObfuscatedString.obfuscate(s).replace(".toString()", "");
    }
}
