package org.truelicense.api.io;

/**
 * Provides a Basic Input/Output System (BIOS).
 *
 * @author Christian Schlichtherle
 */
public interface BiosProvider {

    /** Returns a BIOS. */
    BIOS bios();
}
