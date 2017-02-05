/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.maven.plugin.obfuscation;

import java.util.*;
import java.util.Map.Entry;
import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;
import net.java.truelicense.obfuscate.ObfuscatedString;
import static net.java.truelicense.obfuscate.ObfuscatedString.*;
import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * @author Christian Schlichtherle
 */
final class Obfuscator extends Visitor {

    private static final String
            OBFUSCATED_STRING_INTERNAL_NAME = Type.getInternalName(ObfuscatedString.class);

    private final Map<String, ConstantStringReference>
            csrs = new LinkedHashMap<String, ConstantStringReference>();

    /** Conversion method name. */
    private final String cmn;

    Obfuscator(Processor ctx, ClassVisitor cv) {
        super(ctx, cv);
        cmn = super.internStrings() ? "toStringIntern" : "toString";
    }

    @Override public @CheckForNull FieldVisitor visitField(
            int access,
            String name,
            String desc,
            @CheckForNull String signature,
            @CheckForNull Object value) {
        return new O9n2ndFieldNode(access, name, desc, signature, value);
    }

    @Override public @CheckForNull MethodVisitor visitMethod(
            final int access,
            final String name,
            final String desc,
            final @CheckForNull String signature,
            final @CheckForNull String[] exceptions) {
        final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        return null == mv
                ? null
                : "<init>".equals(name)
                    ? new O9n2ndConstructorVisitor(name, mv)
                    : new O9n2ndMethodVisitor(name, mv);
    }

    @Override public void visitEnd() {
        for (final Entry<String, ConstantStringReference> e : csrs.entrySet())
            addComputationMethod(e.getValue());
        cv.visitEnd();
    }

    private void addComputationMethod(final ConstantStringReference csr) {
        final int access = csr.access;
        final boolean keepField = csr.keepField();
        final boolean isStatic = csr.isStatic();
        final String name = csr.name;
        final String value = csr.value;
        final String lvalue = literal(value);
        final MethodVisitor mv;
        if (keepField && isStatic) {
            mv = cv.visitMethod(access, "<clinit>", "()V", null, null);
            if (null == mv) return;
            logger().debug("Adding method <clinit> to compute \"{}\" and put it into field {}.", lvalue, name);
        } else {
            mv = cv.visitMethod(access, name, "()Ljava/lang/String;", null, null);
            if (null == mv) return;
            logger().debug("Adding method {} to compute \"{}\".", name, lvalue);
        }
        mv.visitCode();
        mv.visitTypeInsn(NEW, OBFUSCATED_STRING_INTERNAL_NAME);
        mv.visitInsn(DUP);
        final long[] obfuscated = array(value);
        addInsn(mv, obfuscated.length);
        mv.visitIntInsn(NEWARRAY, T_LONG);
        for (int i = 0; i < obfuscated.length; i++) {
            mv.visitInsn(DUP);
            addInsn(mv, i);
            mv.visitLdcInsn(obfuscated[i]);
            mv.visitInsn(LASTORE);
        }
        mv.visitMethodInsn(INVOKESPECIAL, OBFUSCATED_STRING_INTERNAL_NAME, "<init>", "([J)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, OBFUSCATED_STRING_INTERNAL_NAME, cmn, "()Ljava/lang/String;", false);
        if (keepField && isStatic) {
            mv.visitFieldInsn(PUTSTATIC, internalClassName(), name, "Ljava/lang/String;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(7, 0);
        } else {
            mv.visitInsn(ARETURN);
            mv.visitMaxs(7, isStatic ? 0 : 1);
        }
        mv.visitEnd();
    }

    private static void addInsn(final MethodVisitor mv, final int index) {
        switch (index) {
            case 0:  mv.visitInsn(ICONST_0); break;
            case 1:  mv.visitInsn(ICONST_1); break;
            case 2:  mv.visitInsn(ICONST_2); break;
            case 3:  mv.visitInsn(ICONST_3); break;
            case 4:  mv.visitInsn(ICONST_4); break;
            case 5:  mv.visitInsn(ICONST_5); break;
            default: mv.visitIntInsn(BIPUSH, index);
        }
    }

    final ConstantStringReference register(
            final ConstantStringReferenceFactory factory,
            final String value) {
        final Map<String, ConstantStringReference> csrs = this.csrs;
        ConstantStringReference csr = csrs.get(value);
        if (null != csr) return csr;
        csrs.put(value, csr = factory.csr(value));
        return csr;
    }

    @Immutable
    private static final class ConstantStringReference {
        final int access;
        final String name, value;

        ConstantStringReference(final int access, final String name, final String value) {
            this.access = access | ACC_SYNTHETIC;
            this.name = name;
            this.value = value;
        }

        boolean keepField() {
            return isProtectedOrPublic() || !isStatic();
        }

        boolean isPrivate() { return 0 != (access & ACC_PRIVATE); }

        boolean isProtectedOrPublic() {
            return 0 != (access & (ACC_PROTECTED | ACC_PUBLIC));
        }

        boolean isStatic() { return 0 != (access & ACC_STATIC); }
    }

    private interface ConstantStringReferenceFactory {
        ConstantStringReference csr(String csv);
    }

    private final class O9n2ndFieldNode
    extends O9nFieldNode implements ConstantStringReferenceFactory {

        O9n2ndFieldNode(
                int access,
                String name,
                String desc,
                @CheckForNull String signature,
                @CheckForNull Object value) {
            super(access, name, desc, signature, value);
        }

        @Override public void visitEnd() {
            final boolean no = needsObfuscation;
            if (obfuscateAll() || no) {
                final String value = stringValue;
                if (null != value) {
                    final ConstantStringReference csr = register(this, value);
                    final String name = csr.name; // shadow field name
                    if (csr.keepField()) {
                        if (no)
                            logger().warn("Obfuscation of protected or public or non-static field {} is insecure because it can't get removed from the byte code.", name);
                        // Fall through.
                    } else {
                        logger().debug("Removing private or package-private static field {}.", name);
                        return; // remove field
                    }
                } else if (no) {
                    logger().error("Annotated field {} does not have a constant string value.", name);
                    // Fall through.
                }
            }
            // Keep field for subsequent processing.
            accept(cv);
        }

        @Override public ConstantStringReference csr(String value) {
            return new ConstantStringReference(access, name, value);
        }
    }

    private class O9n2ndMethodVisitor
    extends MethodVisitor implements ConstantStringReferenceFactory {

        final String localMethodName;

        O9n2ndMethodVisitor(final String name, MethodVisitor mv) {
            super(ASM5, mv);
            this.localMethodName = name;
        }

        @Override public void visitLdcInsn(final Object cst) {
            final String value;
            if (cst instanceof String && replace(value = (String) cst)) {
                process(register(this, value));
            } else {
                mv.visitLdcInsn(cst);
            }
        }

        boolean replace(String value) {
            return obfuscateAll() || constantStrings().contains(value);
        }

        void process(ConstantStringReference csr) {
            final boolean isStatic = csr.isStatic();
            final String name = csr.name;
            final String lvalue = literal(csr.value);
            final String lmn = this.localMethodName;
            final String icn = internalClassName();
            if (!isStatic) mv.visitVarInsn(ALOAD, 0);
            if (readField(csr)) {
                logger().debug("Replacing \"{}\" in method {} with access to field {}.", lvalue, lmn, name);
                final int opcode = isStatic ? GETSTATIC : GETFIELD;
                mv.visitFieldInsn(opcode, icn, name, "Ljava/lang/String;");
            } else {
                logger().debug("Replacing \"{}\" in method {} with call to method {}.", lvalue, lmn, name);
                final int opcode = isStatic
                        ? INVOKESTATIC : csr.isPrivate()
                            ? INVOKESPECIAL : INVOKEVIRTUAL;
                mv.visitMethodInsn(opcode, icn, name, "()Ljava/lang/String;", false);
            }
        }

        boolean readField(ConstantStringReference csr) {
            return csr.keepField();
        }

        @Override public ConstantStringReference csr(String value) {
            return new ConstantStringReference(
                    ACC_PRIVATE | ACC_STATIC,
                    methodName("string", csrs.size()),
                    value);
        }
    }

    private class O9n2ndConstructorVisitor extends O9n2ndMethodVisitor {

        O9n2ndConstructorVisitor(String name, MethodVisitor mv) {
            super(name, mv);
        }

        @Override boolean readField(ConstantStringReference csr) {
            return csr.isProtectedOrPublic() && csr.isStatic();
        }
    }
}
