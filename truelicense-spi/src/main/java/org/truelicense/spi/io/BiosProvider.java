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
