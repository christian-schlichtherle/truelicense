/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.maven.plugin.obfuscation;

import java.util.Set;
import javax.annotation.CheckForNull;
import static net.java.truelicense.obfuscate.ObfuscatedString.*;
import org.objectweb.asm.FieldVisitor;

/**
 * @author Christian Schlichtherle
 */
final class Collector extends Visitor {

    Collector(Processor ctx) { super(ctx, null); }

    @Override
    public @CheckForNull FieldVisitor visitField(int access, String name, String desc, @CheckForNull String signature, @CheckForNull Object value) {
        return value instanceof String
                ? new O9n1stFieldNode(access, name, desc, signature, value)
                : null;
    }

    private final class O9n1stFieldNode extends O9nFieldNode {

        O9n1stFieldNode(int access, String name, String desc, @CheckForNull String signature, @CheckForNull Object value) {
            super(access, name, desc, signature, value);
        }

        @Override
        public void visitEnd() {
            if (!needsObfuscation) return;
            final String value = stringValue;
            if (null == value) return;
            final Set<String> set = constantStrings();
            if (set.contains(value)) return;
            final String lvalue = literal(value);
            logger().debug("Registering constant string {} for obfuscation.", lvalue);
            set.add(value);
        }
    }
}
