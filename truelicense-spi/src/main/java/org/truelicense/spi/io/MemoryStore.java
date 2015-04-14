/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.io;

import org.truelicense.api.io.Store;

import java.io.*;

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

    public byte[] data() { return buffer.clone(); }

    public void data(byte[] buffer) {
        this.buffer = buffer.clone();
    }

    @Override public InputStream input() throws IOException {
        return new ByteArrayInputStream(checkedData());
    }

    @Override public OutputStream output() throws IOException {
        return new ByteArrayOutputStream(bufsize) {
            @Override public void close() throws IOException {
                buffer = toByteArray();
            }
        };
    }

    @Override public void delete() throws IOException {
        checkedData();
        buffer = null;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    private byte[] checkedData() throws FileNotFoundException {
        if (null == buffer) throw new FileNotFoundException();
        return buffer;
    }

    @Override public boolean exists() { return null != buffer; }
}
