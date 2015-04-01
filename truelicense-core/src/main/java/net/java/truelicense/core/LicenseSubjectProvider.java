/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core;

/**
 * Provides a licensing subject.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseSubjectProvider {

    /** Returns the licensing subject. */
    String subject();
}
