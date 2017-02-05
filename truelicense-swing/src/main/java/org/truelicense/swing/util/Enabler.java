/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing.util;

/**
 * A proxy for enabling or disabling an object.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public interface Enabler {

    boolean enabled();

    void enabled(boolean value);
}
