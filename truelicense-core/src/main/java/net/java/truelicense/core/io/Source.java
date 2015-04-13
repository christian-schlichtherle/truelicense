/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.io;

import edu.umd.cs.findbugs.annotations.CreatesObligation;
import java.io.*;

/**
 * A factory for {@link InputStream}s.
 *
 * @see    Sink
 * @author Christian Schlichtherle
 */
public interface Source {

    /**
     * Returns a new input stream for reading the binary data from this source.
     */
    @CreatesObligation InputStream input() throws IOException;
}
