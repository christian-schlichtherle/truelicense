/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.obfuscation.core;

import org.objectweb.asm.FieldVisitor;

import java.util.Set;

import static global.namespace.truelicense.obfuscate.ObfuscatedString.literal;

final class Collector extends Visitor {

    Collector(Processor ctx) {
        super(ctx, null);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String nullableSignature, Object nullableValue) {
        return nullableValue instanceof String
                ? new O9n1stFieldNode(access, name, desc, nullableSignature, nullableValue)
                : null;
    }

    private final class O9n1stFieldNode extends O9nFieldNode {

        O9n1stFieldNode(int access, String name, String desc, String nullableSignature, Object nullableValue) {
            super(access, name, desc, nullableSignature, nullableValue);
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
