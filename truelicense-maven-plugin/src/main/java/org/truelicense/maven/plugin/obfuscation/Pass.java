/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin.obfuscation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Christian Schlichtherle
 */
@NotThreadSafe
abstract class Pass {

    final Processor ctx;

    Pass(final Processor ctx) {
        this.ctx = ctx;
    }

    final Logger logger() { return ctx.logger(); }

    final Logger logger(Node node) { return ctx.logger(node.path()); }

    final boolean obfuscateAll() { return ctx.obfuscateAll(); }

    /** Executes this pass. */
    public void execute() throws IOException {
        scan(new Node("", ctx.directory()));
    }

    private void scan(final Node node) throws IOException {
        final Path file = node.file();
        if (Files.isDirectory(file)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(file)) {
                for (Path member : stream)
                    scan(new Node(node, member.getFileName().toString()));
            }
        } else if (Files.isRegularFile(file) && file.getFileName().endsWith(".class")) {
            process(node);
        } else {
            logger(node).trace("Skipping resource file.");
        }
    }

    abstract void process(final Node node) throws IOException;

    @SuppressFBWarnings("OS_OPEN_STREAM")
    final byte[] read(final Node node) throws IOException {
        final Path file = node.file();
        final byte[] code;
        {
            final long l = Files.size(file);
            final int mb = ctx.maxBytes();
            if (l > mb)
                throw new IOException(
                        String.format("%s (%,d bytes exceeds max %,d bytes)", file, l, mb));
            code = new byte[(int) l];
        }
        {
            try (InputStream in = Files.newInputStream(file)) {
                new DataInputStream(in).readFully(code);
            }
        }
        return code;
    }

    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    final void write(final Node node, final byte[] code) throws IOException {
        try (OutputStream out = Files.newOutputStream(node.file())) {
            out.write(code);
        }
    }
}
