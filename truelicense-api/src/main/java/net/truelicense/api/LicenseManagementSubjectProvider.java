/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * Provides the license management subject.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementSubjectProvider {

    /** Returns the license management subject. */
    String subject();
}
