/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.io;

import org.truelicense.api.io.Store;

/**
 * Provides a store.
 *
 * @author Christian Schlichtherle
 */
public interface StoreProvider {
    /** Returns the store. */
    Store store();
}
