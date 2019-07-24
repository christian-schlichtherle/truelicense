/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.passwd;

/**
 * A container for an erasable password.
 *
 * @see PasswordProtection
 */
public interface Password extends AutoCloseable {

    /** Returns the shared array of password characters. */
    char[] characters();

    /** Erases the shared array of password characters. */
    void close();
}
