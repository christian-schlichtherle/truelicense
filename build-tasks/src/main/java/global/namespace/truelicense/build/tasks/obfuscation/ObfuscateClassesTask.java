/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.obfuscation;

import global.namespace.truelicense.build.tasks.commons.AbstractTask;
import global.namespace.truelicense.build.tasks.commons.CountingLogger;
import global.namespace.truelicense.build.tasks.commons.Logger;
import global.namespace.truelicense.build.tasks.commons.PrefixedLogger;
import global.namespace.truelicense.obfuscate.Obfuscate;
import global.namespace.truelicense.obfuscate.ObfuscatedString;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldNode;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static global.namespace.neuron.di.java.Incubator.wire;
import static global.namespace.truelicense.obfuscate.ObfuscatedString.array;
import static global.namespace.truelicense.obfuscate.ObfuscatedString.literal;
import static java.lang.String.format;
import static java.nio.file.Files.*;
import static java.util.Locale.ENGLISH;
import static org.objectweb.asm.ClassReader.*;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;

/**
 * Obfuscates constant string values in the byte code of Java class files.
 *
 * @see ObfuscatedString
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ObfuscateClassesTask extends AbstractTask {

    private static final int CLASS_READER_FLAGS = SKIP_DEBUG | SKIP_FRAMES | SKIP_CODE;

    private static final String OBFUSCATE_DESCRIPTOR = getDescriptor(Obfuscate.class);

    private static final String OBFUSCATED_STRING_INTERNAL_NAME = getInternalName(ObfuscatedString.class);

    /**
     * A default value for {@link #intern()}, which is {@value}.
     */
    public static final boolean INTERN = true;

    /**
     * A default value for {@link #maxBytes()}, which is {@value}.
     */
    public static final int MAX_BYTES = 64 * 1024;

    /**
     * A default value for {@link #methodNameFormat()}, which is {@value}.
     */
    public static final String METHOD_NAME_FORMAT = "_%s#%d";

    /**
     * Returns whether or not a call to <code>java.lang.String.intern()</code>
     * shall get added when computing the original constant string values again.
     * Use this to preserve the identity relation of constant string values if
     * required.
     */
    public abstract boolean intern();

    /**
     * Returns the maximum allowed size of a class file in bytes.
     */
    public abstract int maxBytes();

    /**
     * Returns the format for synthesized method names.
     * This a format string for the class {@link Formatter}.
     * It's first parameter is a string identifier for the obfuscation stage
     * and its second parameter is an integer index for the synthesized method.
     */
    public abstract String methodNameFormat();

    /**
     * Returns whether or not all constant string values shall get obfuscated.
     * Otherwise only constant string values of fields annotated with {@link Obfuscate} shall get obfuscated.
     */
    private boolean obfuscateAll() {
        return Scope.all == scope();
    }

    /**
     * Returns the directory to scan for class files to process.
     */
    public abstract Path outputDirectory();

    /**
     * Returns the scope of the constant string value obfuscation:
     * <ul>
     * <li>Use <code>none</code> to skip obfuscation.
     * <li>Use <code>annotated</code> to obfuscate only constant string
     *     values of fields which have been annotated with @Obfuscate.
     * <li>Use <code>all</code> to obfuscate all constant string values.
     *     This may result in quite some code size and runtime overhead,
     *     so you should <em>not</em> use this in general.
     * </ul>
     */
    public abstract Scope scope();

    @Override
    public final void execute() throws Exception {
        new Execution().run();
    }

    private String methodName(String stage, int index) {
        return format(ENGLISH, methodNameFormat(), stage, index);
    }

    private static void addInsn(final MethodVisitor mv, final int index) {
        switch (index) {
            case 0:
                mv.visitInsn(ICONST_0);
                break;
            case 1:
                mv.visitInsn(ICONST_1);
                break;
            case 2:
                mv.visitInsn(ICONST_2);
                break;
            case 3:
                mv.visitInsn(ICONST_3);
                break;
            case 4:
                mv.visitInsn(ICONST_4);
                break;
            case 5:
                mv.visitInsn(ICONST_5);
                break;
            default:
                mv.visitIntInsn(BIPUSH, index);
        }
    }

    private class Execution {

        final CountingLogger countingLogger = wire(CountingLogger.class).using(ObfuscateClassesTask.this);

        /**
         * Returns the set of constant strings to obfuscate.
         * The returned set is only used when {@link #obfuscateAll} is {@code false} and is modifiable so as to exchange the
         * set between different processing paths.
         */
        final Set<String> constantStrings = new HashSet<>();

        void run() throws Exception {
            if (scope() == Scope.none) {
                countingLogger.warn("Skipping constant string value obfuscation.");
            } else {
                countingLogger.info(() -> "Obfuscating " + scope() + " constant string values in: " + outputDirectory());
                new FirstPass().run();
                new SecondPass().run();
                if (errorCounter().get() != 0L) {
                    throw new Exception("Errors have been logged.");
                }
            }
        }

        AtomicLong errorCounter() {
            return countingLogger.errorCounter();
        }

        Logger subjectLogger(Node node) {
            return subjectLogger(node.path());
        }

        Logger subjectLogger(String subject) {
            return wire(PrefixedLogger.class)
                    .bind(PrefixedLogger::logger).to(countingLogger)
                    .bind(PrefixedLogger::prefix).to(subject + ": ")
                    .using(this);
        }

        abstract class Pass {

            void run() throws IOException {
                scan(new Node(outputDirectory()));
            }

            void scan(final Node node) throws IOException {
                final Path file = node.file();
                if (isDirectory(file)) {
                    try (DirectoryStream<Path> stream = newDirectoryStream(file)) {
                        for (Path member : stream) {
                            scan(node.updateFile(member));
                        }
                    }
                } else if (isRegularFile(file) && file.toString().endsWith(".class")) {
                    process(node);
                } else {
                    subjectLogger(node).debug("Skipping resource file.");
                }
            }

            abstract void process(final Node node) throws IOException;

            final byte[] read(final Node node) throws IOException {
                final Path file = node.file();
                final byte[] code;
                {
                    final long l = size(file);
                    final int mb = maxBytes();
                    if (l > mb)
                        throw new IOException(format(ENGLISH, "%,d bytes exceeds max %,d bytes in: %s", l, mb, file));
                    code = new byte[(int) l];
                }
                try (InputStream in = newInputStream(file)) {
                    new DataInputStream(in).readFully(code);
                }
                return code;
            }

            final void write(final Node node, final byte[] code) throws IOException {
                try (OutputStream out = newOutputStream(node.file())) {
                    out.write(code);
                }
            }
        }

        final class FirstPass extends Pass {

            @Override
            void run() throws IOException {
                if (obfuscateAll()) {
                    countingLogger.debug("Skipping first pass because all constant string values should get obfuscated.");
                } else {
                    countingLogger.debug("Running first pass.");
                    super.run();
                }
            }

            @Override
            void process(final Node node) throws IOException {
                subjectLogger(node).debug("Analyzing class file.");
                new ClassReader(read(node)).accept(new Collector(), CLASS_READER_FLAGS);
            }
        }

        final class SecondPass extends Pass {

            /**
             * This (virtually) unique prefix is required to make the processor idempotent.
             */
            final String prefix = "clinit@" + System.nanoTime();

            @Override
            void run() throws IOException {
                countingLogger.debug("Running second pass.");
                super.run();
            }

            @Override
            void process(final Node node) throws IOException {
                subjectLogger(node).debug("Transforming class file.");
                final ClassReader cr = new ClassReader(read(node));
                final ClassWriter cw = new ClassWriter(COMPUTE_MAXS);
                cr.accept(new Obfuscator(new ClassInitMerger(prefix, cw)), 0);
                write(node, cw.toByteArray());
            }
        }

        /**
         * Obfuscates constant string values in byte code.
         */
        abstract class O9nClassVisitor extends ClassVisitor {

            /**
             * Cached class access flags.
             */
            int caf;

            /**
             * Cached internal class name.
             */
            String icn;

            /**
             * Cached binary class name.
             */
            String bcn;

            O9nClassVisitor(final ClassVisitor nullableClassVisitor) {
                super(ASM7, nullableClassVisitor);
            }

            final Logger classLogger() {
                return subjectLogger(binaryClassName());
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

            int classAccessFlags() {
                assert null != icn : "Illegal state.";
                return caf;
            }

            final String internalClassName() {
                assert null != icn : "Illegal state.";
                return icn;
            }

            String binaryClassName() {
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
                        if (constantStrings.contains(svalue)) {
                            needsObfuscation = true;
                            this.value = null;
                        }
                    }
                }

                @Override
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    if (OBFUSCATE_DESCRIPTOR.equals(desc)) {
                        needsObfuscation = true;
                        if (value instanceof String) {
                            value = null; // erase
                        }
                        return null;
                    } else {
                        return super.visitAnnotation(desc, visible);
                    }
                }
            }
        }

        final class Collector extends O9nClassVisitor {

            Collector() {
                super(null);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String nullableSignature, Object nullableValue) {
                return nullableValue instanceof String
                        ? new O9n1stFieldNode(access, name, desc, nullableSignature, nullableValue)
                        : null;
            }

            final class O9n1stFieldNode extends O9nFieldNode {

                O9n1stFieldNode(int access, String name, String desc, String nullableSignature, Object nullableValue) {
                    super(access, name, desc, nullableSignature, nullableValue);
                }

                @Override
                public void visitEnd() {
                    if (needsObfuscation) {
                        final String value = stringValue;
                        if (null != value) {
                            final Set<String> set = constantStrings;
                            if (!set.contains(value)) {
                                final String lvalue = literal(value);
                                classLogger().debug(() -> "Registering constant string " + lvalue + " for obfuscation.");
                                set.add(value);
                            }
                        }
                    }
                }
            }
        }

        final class Obfuscator extends O9nClassVisitor {

            final Map<String, ConstantStringReference> csrs = new LinkedHashMap<>();

            /**
             * Conversion method name.
             */
            final String cmn;

            Obfuscator(ClassVisitor cv) {
                super(cv);
                cmn = intern() ? "toStringIntern" : "toString";
            }

            @Override
            public FieldVisitor visitField(
                    int access,
                    String name,
                    String desc,
                    String nullableSignature,
                    Object nullableValue) {
                return new O9n2ndFieldNode(access, name, desc, nullableSignature, nullableValue);
            }

            @Override
            public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String desc,
                    final String nullableSignature,
                    final String[] nullableExceptions) {
                final MethodVisitor mv = cv.visitMethod(access, name, desc, nullableSignature, nullableExceptions);
                return null == mv ? null : "<init>".equals(name)
                        ? new O9n2ndConstructorVisitor(name, mv)
                        : new O9n2ndMethodVisitor(name, mv);
            }

            @Override
            public void visitEnd() {
                for (Map.Entry<String, ConstantStringReference> e : csrs.entrySet()) {
                    addComputationMethod(e.getValue());
                }
                cv.visitEnd();
            }

            void addComputationMethod(final ConstantStringReference csr) {
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
                    classLogger().debug(() -> "Adding method <clinit> to compute \"" + lvalue + "\" and put it into field " + name + ".");
                } else {
                    mv = cv.visitMethod(access, name, "()Ljava/lang/String;", null, null);
                    if (null == mv) return;
                    classLogger().debug(() -> "Adding method " + name + " to compute \"" + lvalue + "\".");
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

            ConstantStringReference register(final ConstantStringReferenceFactory factory, final String value) {
                final Map<String, ConstantStringReference> csrs = this.csrs;
                ConstantStringReference csr = csrs.get(value);
                if (null == csr) {
                    csr = factory.csr(value);
                    csrs.put(value, csr);
                }
                return csr;
            }

            final class O9n2ndFieldNode extends O9nFieldNode implements ConstantStringReferenceFactory {

                O9n2ndFieldNode(
                        int access,
                        String name,
                        String desc,
                        String nullableSignature,
                        Object nullableValue) {
                    super(access, name, desc, nullableSignature, nullableValue);
                }

                @Override
                public void visitEnd() {
                    final boolean no = needsObfuscation;
                    if (obfuscateAll() || no) {
                        final String value = stringValue;
                        if (null != value) {
                            final ConstantStringReference csr = register(this, value);
                            final String name = csr.name; // shadow field name
                            if (csr.keepField()) {
                                if (no) {
                                    classLogger().warn(() -> "Obfuscation of protected or public or non-static field " + name + " is insecure because it can't get removed from the byte code.");
                                }
                                // Fall through.
                            } else {
                                classLogger().debug(() -> "Removing private or package-private static field " + name + ".");
                                return; // remove field
                            }
                        } else if (no) {
                            classLogger().error(() -> "Annotated field " + name + " does not have a constant string value.");
                            // Fall through.
                        }
                    }
                    // Keep field for subsequent processing.
                    accept(cv);
                }

                @Override
                public ConstantStringReference csr(String value) {
                    return new ConstantStringReference(access, name, value);
                }
            }

            class O9n2ndMethodVisitor extends MethodVisitor implements ConstantStringReferenceFactory {

                final String localMethodName;

                O9n2ndMethodVisitor(final String name, MethodVisitor mv) {
                    super(ASM7, mv);
                    this.localMethodName = name;
                }

                @Override
                public void visitLdcInsn(final Object cst) {
                    final String value;
                    if (cst instanceof String && replace(value = (String) cst)) {
                        process(register(this, value));
                    } else {
                        mv.visitLdcInsn(cst);
                    }
                }

                boolean replace(String value) {
                    return obfuscateAll() || constantStrings.contains(value);
                }

                void process(ConstantStringReference csr) {
                    final boolean isStatic = csr.isStatic();
                    final String name = csr.name;
                    final String lvalue = literal(csr.value);
                    final String lmn = this.localMethodName;
                    final String icn = internalClassName();
                    if (!isStatic) {
                        mv.visitVarInsn(ALOAD, 0);
                    }
                    if (readField(csr)) {
                        classLogger().debug(() -> "Replacing \"" + lvalue + "\" in method " + lmn + " with access to field " + name + ".");
                        final int opcode = isStatic ? GETSTATIC : GETFIELD;
                        mv.visitFieldInsn(opcode, icn, name, "Ljava/lang/String;");
                    } else {
                        classLogger().debug(() -> "Replacing \"" + lvalue + "\" in method " + lmn + " with call to method " + name + ".");
                        final int opcode = isStatic ? INVOKESTATIC : csr.isPrivate() ? INVOKESPECIAL : INVOKEVIRTUAL;
                        mv.visitMethodInsn(opcode, icn, name, "()Ljava/lang/String;", false);
                    }
                }

                boolean readField(ConstantStringReference csr) {
                    return csr.keepField();
                }

                @Override
                public ConstantStringReference csr(String value) {
                    return new ConstantStringReference(
                            ACC_PRIVATE | ACC_STATIC,
                            methodName("string", csrs.size()),
                            value);
                }
            }

            final class O9n2ndConstructorVisitor extends O9n2ndMethodVisitor {

                O9n2ndConstructorVisitor(String name, MethodVisitor mv) {
                    super(name, mv);
                }

                @Override
                boolean readField(ConstantStringReference csr) {
                    return csr.isProtectedOrPublic() && csr.isStatic();
                }
            }
        }

        /**
         * Merges {@code <clinit>} methods.
         * This class visitor requires the flag {@link org.objectweb.asm.ClassWriter#COMPUTE_MAXS} to be set.
         */
        final class ClassInitMerger extends O9nClassVisitor {

            final String prefix;
            O9nInitMethodVisitor imv;
            int counter;

            ClassInitMerger(final String prefix, final ClassVisitor cv) {
                super(cv);
                this.prefix = prefix;
            }

            @Override
            public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String desc,
                    final String nullableSignature,
                    final String[] nullableExceptions) {
                MethodVisitor mv;
                if ("<clinit>".equals(name)) {
                    if (isInterface()) {
                        mv = imv;
                        if (null == mv) {
                            mv = cv.visitMethod(access, name, desc, nullableSignature, nullableExceptions);
                            if (null != mv) {
                                mv = imv = new O9nInterfaceInitMethodVisitor(mv);
                            }
                        }
                    } else {
                        final String n = methodName(prefix, counter++);
                        mv = cv.visitMethod(
                                ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
                                n,
                                desc,
                                nullableSignature,
                                nullableExceptions);
                        O9nInitMethodVisitor imv = this.imv;
                        if (null == imv) {
                            imv = this.imv = new O9nInitMethodVisitor(
                                    cv.visitMethod(access, name, desc, nullableSignature, nullableExceptions));
                            imv.visitCode();
                        }
                        imv.visitMethodInsn(INVOKESTATIC, internalClassName(), n, desc, false);
                    }
                } else {
                    mv = cv.visitMethod(access, name, desc, nullableSignature, nullableExceptions);
                }
                return mv;
            }

            @Override
            public void visitEnd() {
                final O9nInitMethodVisitor imv = this.imv;
                if (null != imv) {
                    imv.visitInitEnd();
                }
                cv.visitEnd();
            }
        }
    }
}
