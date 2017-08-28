/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.maven.plugin.obfuscation.core;

import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;

import java.io.IOException;

import static org.objectweb.asm.ClassReader.*;

/**
 * @author Christian Schlichtherle
 */
final class FirstPass extends Pass {

    private static final int
            CLASS_READER_FLAGS = SKIP_DEBUG | SKIP_FRAMES | SKIP_CODE;

    FirstPass(Processor ctx) { super(ctx); }

    @Override
    public void execute() throws IOException {
        final Logger logger = logger();
        if (obfuscateAll()) {
            logger.debug("Skipping first pass because all constant string values should get obfuscated.");
        } else {
            logger.debug("Running first pass.");
            super.execute();
        }
    }

    @Override
    void process(final Node node) throws IOException {
        logger(node).trace("Analyzing class file.");
        new ClassReader(read(node)).accept(ctx.collector(), CLASS_READER_FLAGS);
    }
}
