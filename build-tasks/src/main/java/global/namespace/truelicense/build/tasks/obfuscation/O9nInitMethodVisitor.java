/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.obfuscation;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.RETURN;

class O9nInitMethodVisitor extends MethodVisitor {

    O9nInitMethodVisitor(MethodVisitor mv) {
        super(ASM7, mv);
    }

    @Override
    public final void visitMaxs(int maxStack, int maxLocals) {
    }

    @Override
    public final void visitEnd() {
    }

    final void visitInitEnd() {
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0); // let compute
        mv.visitEnd();
    }
}
