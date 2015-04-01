/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.maven.plugin.obfuscation;

import java.io.IOException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.ClassWriter.*;

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
    public void run() {
        logger().debug("Running second pass.");
        super.run();
    }

    @Override
    void process(final Node node) {
        logger(node).trace("Transforming class file.");
        try {
            final ClassReader cr = new ClassReader(read(node));
            final ClassWriter cw = new ClassWriter(COMPUTE_MAXS);
            cr.accept(ctx.obfuscator(ctx.merger(cw, prefix)), 0);
            write(node, cw.toByteArray());
        } catch (IOException ex) {
            logger().error(ex.toString(), ex);
        }
    }
}
