/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.jax.rs.dto;

import global.namespace.truelicense.api.License;

/**
 * @author Christian Schlichtherle
 */
public class LicenseDTO {

    public License license;

    public LicenseDTO() { }

    public LicenseDTO(final License license) { this.license = license; }
}
