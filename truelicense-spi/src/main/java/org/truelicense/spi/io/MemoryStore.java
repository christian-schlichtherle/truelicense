/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.io;

import org.truelicense.api.io.Store;
import org.truelicense.spi.misc.Option;

import java.io.*;
import java.util.List;

/**
 * A (heap) memory store.
 *
 * @author Christian Schlichtherle
 */
public final class MemoryStore implements Store {

    private final int bufsize;

    private List<byte[]> optBuffer = Option.none();

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
                data(toByteArray());
            }
        };
    }

    @Override
    public void delete() throws IOException {
        checkedData();
        optBuffer = Option.none();
    }

    @Override
    public boolean exists() { return !optBuffer.isEmpty(); }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private byte[] checkedData() throws FileNotFoundException {
        for (byte[] buffer : optBuffer)
            return buffer;
        throw new FileNotFoundException();
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public byte[] data() {
        for (byte[] buffer : optBuffer)
            return buffer.clone();
        throw new IllegalStateException();
    }

    public void data(byte[] buffer) { optBuffer = Option.wrap(buffer.clone()); }
}
