/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.io;

import java.io.*;
import javax.annotation.*;

/**
 * A (heap) memory store.
 *
 * @author Christian Schlichtherle
 */
public final class MemoryStore implements Store {

    private final int bufsize;

    private byte[] buffer;

    /**
     * Equivalent to <code>new {@link #MemoryStore(int)
     * MemoryStore(BUFSIZE)}</code>.
     *
     * @see Store#BUFSIZE
     */
    public MemoryStore() { this(BUFSIZE); }

    /**
     * Constructs a memory store with the given data size to use upon
     * {@link #output}.
     *
     * @param bufsize the data size to use upon {@link #output}.
     */
    public MemoryStore(final int bufsize) {
        if (0 > (this.bufsize = bufsize)) throw new IllegalArgumentException();
    }

    @Override
    public InputStream input() throws IOException {
        return new ByteArrayInputStream(checkedData());
    }

    @Override
    public OutputStream output() throws IOException {
        return new ByteArrayOutputStream(bufsize) {
            @Override public void close() throws IOException {
                buffer = toByteArray();
            }
        };
    }

    @Override
    public void delete() throws IOException {
        checkedData();
        buffer = null;
    }

    @Override
    public boolean exists() { return null != buffer; }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    private byte[] checkedData() throws FileNotFoundException {
        if (null == buffer) throw new FileNotFoundException();
        return buffer;
    }

    public @Nullable byte[] data() { return clone(buffer); }

    public void data(@CheckForNull byte[] buffer) {
        this.buffer = clone(buffer);
    }

    private static byte[] clone(@CheckForNull byte[] buffer) {
        return null == buffer ? null : buffer.clone();
    }
}
