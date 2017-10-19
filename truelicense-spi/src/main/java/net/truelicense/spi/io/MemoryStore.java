/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.io;

import net.truelicense.api.codec.Codec;
import net.truelicense.api.io.Store;
import net.truelicense.spi.codec.SerializationCodec;

import java.io.*;
import java.util.Optional;

/**
 * A (heap) memory store.
 *
 * @author Christian Schlichtherle
 */
public final class MemoryStore implements Store {

    private final int bufsize;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<byte[]> optBuffer = Optional.empty();

    /**
     * Equivalent to <code>new {@link #MemoryStore(int) MemoryStore(BUFSIZE)}</code>.
     *
     * @see Store#BUFSIZE
     */
    public MemoryStore() {
        this(BUFSIZE);
    }

    /**
     * Constructs a memory store with the given data size to use upon
     * {@link #output}.
     *
     * @param bufsize the data size to use upon {@link #output}.
     */
    public MemoryStore(final int bufsize) {
        if (0 > (this.bufsize = bufsize)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public InputStream input() throws IOException {
        return new ByteArrayInputStream(checkedData());
    }

    @Override
    public OutputStream output() throws IOException {
        return new ByteArrayOutputStream(bufsize) {
            @Override
            public void close() throws IOException {
                data(toByteArray());
            }
        };
    }

    @Override
    public void delete() throws IOException {
        checkedData();
        optBuffer = Optional.empty();
    }

    @Override
    public boolean exists() {
        return optBuffer.isPresent();
    }

    private byte[] checkedData() throws FileNotFoundException {
        return optBuffer.orElseThrow(FileNotFoundException::new);
    }

    public byte[] data() {
        return optBuffer.map(byte[]::clone).orElseThrow(IllegalStateException::new);
    }

    public MemoryStore data(final byte[] buffer) {
        optBuffer = Optional.ofNullable(buffer.clone());
        return this;
    }

    /** Returns a clone of the given object using a new {@link SerializationCodec} and this memory store. */
    public <T> T clone(T object) throws Exception { return clone(object, new SerializationCodec()); }

    /** Returns a clone of the given object using the given codec and this memory store. */
    public <T> T clone(final T object, final Codec codec) throws Exception {
        codec.encoder(this).encode(object);
        return codec.decoder(this).decode(object.getClass());
    }
}
