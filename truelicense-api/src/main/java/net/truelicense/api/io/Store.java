/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.OptionalLong;

/**
 * An abstraction for storing data.
 *
 * @author Christian Schlichtherle
 */
public interface Store extends Source, Sink {

    /** The default buffer size, which is {@value}. */
    int BUFSIZE = 8 * 1024;

    /** Deletes the data. */
    void delete() throws IOException;

    /** Returns the size of the data in the store iff it exists, otherwise empty. */
    OptionalLong size() throws IOException;

    /** Returns {@code true} if and only if the data exists. */
    default boolean exists() throws IOException { return size().isPresent(); }

    /**
     * Returns a new store which (un-)applies the given transformation to the I/O streams provided by this store.
     *
     * @param t the transformation to (un-)apply to the I/O streams provided by this store.
     */
    default Store with(Transformation t) {
        return new Store() {

            @Override
            public InputStream input() throws IOException { return t.unapply(Store.this).input(); }

            @Override
            public OutputStream output() throws IOException { return t.apply(Store.this).output(); }

            @Override
            public void delete() throws IOException { Store.this.delete(); }

            @Override
            public OptionalLong size() throws IOException { return Store.this.size(); }
        };
    }
}
