/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.io;

/**
 * Provides a Basic Input/Output System (BIOS).
 *
 * @author Christian Schlichtherle
 */
public interface BiosProvider {

    /** Returns the BIOS. */
    BIOS bios();
}
