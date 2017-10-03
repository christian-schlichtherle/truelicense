/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.io;

import java.io.*;

/**
 * A factory for {@link InputStream}s.
 *
 * @see    Sink
 * @author Christian Schlichtherle
 */
public interface Source {

    /** Returns a new input stream for reading data from this source. */
    InputStream input() throws IOException;
}
