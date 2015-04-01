/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.maven.plugin.obfuscation;

import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileOutputStream;
import java.io.*;
import javax.annotation.concurrent.NotThreadSafe;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;

/**
 * @author Christian Schlichtherle
 */
@NotThreadSafe
abstract class Pass implements Runnable {

    final Processor ctx;

    Pass(final Processor ctx) {
        this.ctx = ctx;
    }

    final Logger logger() { return ctx.logger(); }

    final Logger logger(Node node) { return ctx.logger(node.path()); }

    final boolean obfuscateAll() { return ctx.obfuscateAll(); }

    @Override
    public void run() { scan(new Node("", ctx.directory())); }

    private void scan(final Node node) {
        final File file = node.file();
        if (file.isDirectory()) {
            final String[] members = file.list();
            if (null != members) {
                for (String member : members)
                    scan(new Node(node, member));
            } else {
                logger(node).error("Cannot list directory.");
            }
        } else if (file.isFile() && file.getName().endsWith(".class")) {
            process(node);
        } else {
            logger(node).trace("Skipping resource file.");
        }
    }

    abstract void process(final Node node);

    @SuppressFBWarnings("OS_OPEN_STREAM")
    final byte[] read(final Node node) throws IOException {
        final File file = node.file();
        final byte[] code;
        {
            final long l = file.length();
            final int mb = ctx.maxBytes();
            if (l > mb)
                throw new IOException(
                        String.format("%s (%,d bytes exceeds max %,d bytes)", file, l, mb));
            code = new byte[(int) l];
        }
        {
            final InputStream in = new TFileInputStream(file);
            try { new DataInputStream(in).readFully(code); }
            finally { in.close(); }
        }
        return code;
    }

    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    final void write(final Node node, final byte[] code) throws IOException {
        final OutputStream out = new TFileOutputStream(node.file());
        try { out.write(code); }
        finally { out.close(); }
    }
}
