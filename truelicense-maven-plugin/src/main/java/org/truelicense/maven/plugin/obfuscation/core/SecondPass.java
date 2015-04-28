/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin.obfuscation.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * @author Christian Schlichtherle
 */
final class SecondPass extends Pass {

    /**
     * This (virtually) unique prefix is required to make the processor
     * idempotent.
     */
    private final String prefix = "clinit@" + System.currentTimeMillis();

    SecondPass(Processor ctx) { super(ctx); }

    @Override
    public void execute() throws IOException {
        logger().debug("Running second pass.");
        super.execute();
    }

    @Override
    void process(final Node node) throws IOException {
        logger(node).trace("Transforming class file.");
        final ClassReader cr = new ClassReader(read(node));
        final ClassWriter cw = new ClassWriter(COMPUTE_MAXS);
        cr.accept(ctx.obfuscator(ctx.merger(cw, prefix)), 0);
        write(node, cw.toByteArray());
    }
}
