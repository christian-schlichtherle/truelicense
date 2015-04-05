/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin.obfuscation;

import java.util.Set;
import javax.annotation.*;
import javax.annotation.concurrent.NotThreadSafe;
import org.truelicense.obfuscate.Obfuscate;
import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.tree.FieldNode;
import org.slf4j.Logger;

/**
 * Obfuscates constant string values in byte code.
 *
 * @author Christian Schlichtherle
 */
@NotThreadSafe
abstract class Visitor extends ClassVisitor {

    private static final String
            OBFUSCATE_DESCRIPTOR = Type.getDescriptor(Obfuscate.class);

    private final Processor ctx;

    /** Cached class access flags. */
    private int caf;

    /** Cached internal class name. */
    private @Nullable String icn;

    /** Cached binary class name. */
    private @Nullable String bcn;

    Visitor(final Processor ctx, final @CheckForNull ClassVisitor cv) {
        super(ASM4, cv);
        this.ctx = ctx;
    }

    final Logger logger() { return ctx.logger(binaryClassName()); }

    final boolean obfuscateAll() { return ctx.obfuscateAll(); }

    final boolean internStrings() { return ctx.internStrings(); }

    final String methodName(String stage, int index) {
        return ctx.methodName(stage, index);
    }

    final Set<String> constantStrings() {
        return ctx.constantStrings();
    }

    @Override
    public final void visit(
            final int version,
            final int access,
            final String name,
            final @CheckForNull String signature,
            final @CheckForNull String superName,
            final @CheckForNull String[] interfaces) {
        bcn = Type.getObjectType(icn = name).getClassName();
        super.visit(version, caf = access, name, signature, superName, interfaces);
    }

    final boolean isInterface() {
        return 0 != (classAccessFlags() & ACC_INTERFACE);
    }

    final int classAccessFlags() {
        assert null != icn : "Illegal state.";
        return caf;
    }

    final String internalClassName() {
        assert null != icn : "Illegal state.";
        return icn;
    }

    final String binaryClassName() {
        assert null != bcn : "Illegal state.";
        return bcn;
    }

    @SuppressWarnings({ "PackageVisibleInnerClass", "PackageVisibleField" })
    class O9nFieldNode extends FieldNode {

        boolean needsObfuscation;
        @CheckForNull String stringValue;

        O9nFieldNode(
                final int access,
                final String name,
                final String desc,
                final @CheckForNull String signature,
                final @CheckForNull Object value) {
            super(ASM4, access, name, desc, signature, value);
            if (value instanceof String) {
                final String svalue = (String) value;
                this.stringValue = svalue;
                if (constantStrings().contains(svalue)) {
                    needsObfuscation = true;
                    this.value = null;
                }
            }
        }

        @Override
        public AnnotationVisitor visitAnnotation(
                final String desc,
                final boolean visible) {
            if (OBFUSCATE_DESCRIPTOR.equals(desc)) {
                needsObfuscation = true;
                if (value instanceof String) value = null; // erase
                return null;
            } else {
                return super.visitAnnotation(desc, visible);
            }
        }
    }
}
