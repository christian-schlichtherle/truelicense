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
        current = new ComposedTransformation(get(), next);
        return this;
    }

    /**
     * Returns a new sink which applies the composed transformation to the
     * given sink.
     */
    public Sink to(Sink sink) {
        return store(sink);
    }

    /**
     * Returns a new source which applies the composed transformation to the
     * given source.
     */
    public Source to(Source source) {
        return store(source);
    }

    /**
     * Returns a new store which applies the composed transformation to the
     * given store.
     */
    public Store to(Store store) {
        return store(store);
    }

    private Store store(Object sinkOrSourceOrStore) {
        return new TransformedStore(sinkOrSourceOrStore, get());
    }

    private final static class ComposedTransformation implements Transformation {

        final Transformation previous, next;

        ComposedTransformation(final Transformation previous,
                               final Transformation next) {
            this.previous = previous;
            this.next = next;
        }

        @Override
        public Sink apply(Sink sink) {
            return previous.apply(next.apply(sink));
        }

        @Override
        public Source unapply(Source source) {
            return previous.unapply(next.unapply(source));
        }
    }

    private final static class TransformedStore implements Store {

        final Object sinkOrSourceOrStore;
        final Transformation transformation;

        TransformedStore(final Object sinkOrSourceOrStore,
                         final Transformation transformation) {
            this.sinkOrSourceOrStore = sinkOrSourceOrStore;
            this.transformation = transformation;
        }

        @Override
        public void delete() throws IOException {
            ((Store) sinkOrSourceOrStore).delete();
        }

        @Override
        public boolean exists() throws IOException {
            return ((Store) sinkOrSourceOrStore).exists();
        }

        @Override
        public InputStream input() throws IOException {
            return transformation.unapply((Source) sinkOrSourceOrStore).input();
        }

        @Override
        public OutputStream output() throws IOException {
            return transformation.apply((Sink) sinkOrSourceOrStore).output();
        }
    }
}
