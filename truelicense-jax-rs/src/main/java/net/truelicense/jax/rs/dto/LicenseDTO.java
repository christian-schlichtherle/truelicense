package net.truelicense.jax.rs.dto;

import net.truelicense.api.License;

public class LicenseDTO {

    public License license;

    public LicenseDTO() { }

    public LicenseDTO(final License license) { this.license = license; }
}
