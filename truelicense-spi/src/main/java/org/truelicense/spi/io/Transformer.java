/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.io;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.io.Transformation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Composes multiple transformations into one and optionally applies it to a
 * {@link Store}.
 * Usage example:
 * <pre>{@code
 *  Transformation compression = ...;
 *  Transformation encryption = ...;
 *  Store store = ...;
 *  Store storeWithTransformationsApplied = Transformer.apply(compression).then(encryption).to(store);
 * }</pre>
 * <p>
 * You can also call it's {@link #get()} method if you just want the composed
 * transformation:
 * <pre>{@code
 *  Transformation compression = ...;
 *  Transformation encryption = ...;
 *  Transformation composed = Transformer.apply(compression).then(encryption).get();
 * }</pre>
 */
public final class Transformer {

    private Transformation current;

    /**
     * Returns a transformation builder with the given transformation as its
     * initial composed transformation.
     */
    public static Transformer apply(Transformation initial) {
        return new Transformer(initial);
    }

    private Transformer(final Transformation transformation) {
        this.current = Objects.requireNonNull(transformation);
    }

    /** Returns the composed transformation. */
    public Transformation get() { return current; }

    /**
     * Creates a new transformation which applies the given transformation
     * <em>after</em> the composed transformation and replaces the composed
     * transformation with this new transformation.
     *
     * @return {@code this}
     */
    public Transformer then(final Transformation next) {
        current = new Transformation() {

            final Transformation previous = get();

            @Override
            public Sink apply(Sink sink) {
                return previous.apply(next.apply(sink));
            }

            @Override
            public Source unapply(Source source) {
                return previous.unapply(next.unapply(source));
            }
        };
        return this;
    }

    /**
     * Returns a new store which applies the composed transformation to the
     * given store.
     */
    public Store to(final Store store) {
        return new Store() {

            final Transformation transformation = get();

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
                return transformation.unapply(store).input();
            }

            @Override
            public OutputStream output() throws IOException {
                return transformation.apply(store).output();
            }
        };
    }
}
