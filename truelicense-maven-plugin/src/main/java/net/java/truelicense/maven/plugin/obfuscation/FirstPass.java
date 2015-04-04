/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.maven.plugin.obfuscation;

import java.io.IOException;
import org.objectweb.asm.ClassReader;
import static org.objectweb.asm.ClassReader.*;
import org.slf4j.Logger;

/**
 * @author Christian Schlichtherle
 */
final class FirstPass extends Pass {

    private static final int
            CLASS_READER_FLAGS = SKIP_DEBUG | SKIP_FRAMES | SKIP_CODE;

    FirstPass(Processor ctx) { super(ctx); }

    @Override
    public void run() {
        final Logger logger = logger();
        if (obfuscateAll()) {
            logger.debug("Skipping first pass because all constant string values should get obfuscated.");
        } else {
            logger.debug("Running first pass.");
            super.run();
        }
    }

    @Override
    void process(final Node node) {
        logger(node).trace("Analyzing class file.");
        try {
            new ClassReader(read(node)).accept(ctx.collector(),
                    CLASS_READER_FLAGS);
        } catch (IOException ex) {
            logger().error(ex.toString(), ex);
        }
    }
}
