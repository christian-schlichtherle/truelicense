/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.io;

import net.truelicense.api.io.Sink;
import net.truelicense.api.io.Source;
import net.truelicense.api.io.Transformation;

/**
 * @author Christian Schlichtherle
 */
final class ComposedTransformation implements Transformation {

    private final Transformation previous, next;

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
