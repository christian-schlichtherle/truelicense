package org.truelicense.api;

/**
 * A runtime exception which wraps a (checked)
 * {@link LicenseManagementException}.
 * This is generally thrown by the {@code Unchecked*} interfaces.
 *
 * @author Christian Schlichtherle
 */
public class UncheckedLicenseManagementException extends RuntimeException {

    private static final long serialVersionUID = 0L;

    public UncheckedLicenseManagementException(LicenseManagementException e) {
        super(e);
    }
}
