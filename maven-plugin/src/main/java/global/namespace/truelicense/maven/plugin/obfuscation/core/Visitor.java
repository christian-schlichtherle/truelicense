/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.obfuscation.core;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.slf4j.Logger;
import global.namespace.truelicense.obfuscate.Obfuscate;

import java.util.Set;

import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ASM7;

/**
 * Obfuscates constant string values in byte code.
 */
abstract class Visitor extends ClassVisitor {

    private static final String
            OBFUSCATE_DESCRIPTOR = Type.getDescriptor(Obfuscate.class);

    private final Processor ctx;

    /**
     * Cached class access flags.
     */
    private int caf;

    /**
     * Cached internal class name.
     */
    private String icn;

    /**
     * Cached binary class name.
     */
    private String bcn;

    Visitor(final Processor ctx, final ClassVisitor nullableClassVisitor) {
        super(ASM7, nullableClassVisitor);
        this.ctx = ctx;
    }

    final Logger logger() {
        return ctx.logger(binaryClassName());
    }

    final boolean obfuscateAll() {
        return ctx.obfuscateAll();
    }

    final boolean internStrings() {
        return ctx.internStrings();
    }

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
            final String nullableSignature,
            final String nullableSuperName,
            final String[] nullableInterfaces) {
        bcn = Type.getObjectType(icn = name).getClassName();
        super.visit(version, caf = access, name, nullableSignature, nullableSuperName, nullableInterfaces);
    }

    final boolean isInterface() {
        return 0 != (classAccessFlags() & ACC_INTERFACE);
    }

    private int classAccessFlags() {
        assert null != icn : "Illegal state.";
        return caf;
    }

    final String internalClassName() {
        assert null != icn : "Illegal state.";
        return icn;
    }

    private String binaryClassName() {
        assert null != bcn : "Illegal state.";
        return bcn;
    }

    class O9nFieldNode extends FieldNode {

        boolean needsObfuscation;
        String stringValue;

        O9nFieldNode(
                final int access,
                final String name,
                final String desc,
                final String nullableSignature,
                final Object nullableValue) {
            super(ASM7, access, name, desc, nullableSignature, nullableValue);
            if (nullableValue instanceof String) {
                final String svalue = (String) nullableValue;
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
