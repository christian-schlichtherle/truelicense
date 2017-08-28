/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.io;

import net.truelicense.api.io.Sink;
import net.truelicense.api.io.Source;
import net.truelicense.api.io.Store;
import net.truelicense.api.io.Transformation;

import java.util.Objects;

/**
 * Composes multiple transformations into one and optionally applies/un-applies
 * it to {@link Sink}s, {@link Source}s or {@link Store}s.
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

    private Transformation composed;

    /**
     * Returns a transformation builder with the given transformation as its
     * initial composed transformation.
     */
    public static Transformer apply(Transformation t) {
        return new Transformer(t);
    }

    private Transformer(final Transformation t) {
        this.composed = Objects.requireNonNull(t);
    }

    /** Returns the composed transformation. */
    public Transformation get() { return composed; }

    /**
     * Creates a new transformation which applies the given transformation
     * <em>after</em> the composed transformation and replaces the composed
     * transformation with this new transformation.
     *
     * @return {@code this}
     */
    public Transformer then(final Transformation next) {
        composed = new ComposedTransformation(get(), next);
        return this;
    }

    /**
     * Returns a new sink which applies the composed transformation to the
     * given sink.
     */
    public Sink to(Sink sink) { return get().apply(sink); }

    /**
     * Returns a new source which un-applies the composed transformation to the
     * given source.
     */
    public Source to(Source source) { return get().unapply(source); }

    /**
     * Returns a new store which applies/un-applies the composed transformation
     * to the given store.
     */
    public Store to(Store store) { return new TransformedStore(get(), store); }
}
