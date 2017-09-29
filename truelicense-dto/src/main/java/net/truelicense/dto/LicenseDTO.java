package net.truelicense.dto;

import net.truelicense.api.License;

public class LicenseDTO {

    public License license;

    public LicenseDTO() { }

    public LicenseDTO(final License license) { this.license = license; }
}
