/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.swing.util;

/**
 * A proxy for enabling or disabling an object.
 */
public interface Enabler {

    boolean enabled();

    void enabled(boolean value);
}
