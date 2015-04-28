/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin.generation;

import org.truelicense.obfuscate.ObfuscatedString;

/**
 * An immutable Velocity tool for use with Scala template files.
 *
 * @author Christian Schlichtherle
 */
public final class ScalaTool {

    public String obfuscatedString(String s) {
        return ObfuscatedString.obfuscate(s)
                .replace("new long[] { ", "Array[Long](")
                .replace(" }).toString()", "))");
    }
}
