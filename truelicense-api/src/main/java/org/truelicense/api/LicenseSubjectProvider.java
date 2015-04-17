/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

/**
 * Provides a license subject.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseSubjectProvider {

    /** Returns the license subject. */
    String subject();
}
