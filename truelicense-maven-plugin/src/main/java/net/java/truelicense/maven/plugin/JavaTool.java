package net.java.truelicense.maven.plugin;

import net.java.truelicense.obfuscate.ObfuscatedString;

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
