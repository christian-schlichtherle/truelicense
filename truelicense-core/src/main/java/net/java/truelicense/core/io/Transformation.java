/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.io;

/**
 * A transformation for the I/O streams provided by {@linkplain Sink sinks} and
 * {@linkplain Source sources}.
 *
 * @author Christian Schlichtherle
 */
public interface Transformation {

    /**
     * Returns a sink which applies the effect of this transformation to
     * the output streams provided by the given sink.
     *
     * @param  sink the sink with the output streams to which the effect
     *         of this I/O transformation shall get applied.
     * @return A sink which applies the effect of this transformation to
     *         the output streams provided by the given sink.
     */
    Sink apply(Sink sink);

    /**
     * Returns a source which unapplies the effect of this transformation from
     * the input streams provided by the given source.
     *
     * @param  source the source with the input streams from which the effect
     *         of this I/O transformation shall get removed.
     * @return A source which unapplies the effect of this transformation from
     *         the input streams provided by the given source.
     */
    Source unapply(Source source);
}
