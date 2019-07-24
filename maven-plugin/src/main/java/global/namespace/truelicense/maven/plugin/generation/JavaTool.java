/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.maven.plugin.generation;

import global.namespace.truelicense.obfuscate.ObfuscatedString;

/**
 * An immutable Velocity tool for use with Java template files.
 *
 * @author Christian Schlichtherle
 */
public final class JavaTool {

    public String obfuscatedString(String s) {
        return ObfuscatedString
                .obfuscate(s)
                .replace(".toString()", "");
    }
}
