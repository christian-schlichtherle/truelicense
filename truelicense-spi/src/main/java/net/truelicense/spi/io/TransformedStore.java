/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.io;

import net.truelicense.api.io.Sink;
import net.truelicense.api.io.Source;
import net.truelicense.api.io.Store;
import net.truelicense.api.io.Transformation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Christian Schlichtherle
 */
final class TransformedStore implements Store {

    private final Sink sink;
    private final Source source;
    private final Store store;

    TransformedStore(final Transformation t, final Store s) {
        this.sink = t.apply(s);
        this.source = t.unapply(s);
        this.store = s;
    }

    @Override
    public void delete() throws IOException {
        store.delete();
    }

    @Override
    public boolean exists() throws IOException {
        return store.exists();
    }

    @Override
    public InputStream input() throws IOException {
        return source.input();
    }

    @Override
    public OutputStream output() throws IOException {
        return sink.output();
    }
}
