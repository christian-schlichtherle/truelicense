/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.passwd;

/**
 * A container for a destroyable password.
 * Use this with the try-with-resources statement to ensure that the password
 * gets destroyed after use.
 *
 * @author Christian Schlichtherle
 */
public interface Password extends AutoCloseable {

    /** Returns the shared array of password characters. */
    char[] characters();

    /** Wipes the shared array of password characters. */
    void close();
}
