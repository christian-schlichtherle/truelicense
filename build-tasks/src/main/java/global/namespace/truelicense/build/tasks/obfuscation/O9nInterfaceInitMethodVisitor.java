/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.obfuscation;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.RETURN;

final class O9nInterfaceInitMethodVisitor extends O9nInitMethodVisitor {

    O9nInterfaceInitMethodVisitor(MethodVisitor mv) {
        super(mv);
    }

    @Override
    public void visitInsn(int opcode) {
        if (RETURN != opcode) mv.visitInsn(opcode);
    }
}
