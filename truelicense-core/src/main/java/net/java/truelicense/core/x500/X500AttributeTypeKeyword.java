/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core.x500;

/**
 * The predefined X.500 attribute type keywords for the classes
 * {@link X500PrincipalBuilder} and
 * {@link javax.security.auth.x500.X500Principal}.
 *
 * @author Christian Schlichtherle
 * @see <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>
 * @see <a href="http://www.ietf.org/rfc/rfc2459.txt">RFC 2459</a>
 * @since TrueLicense 2.3
 */
public enum X500AttributeTypeKeyword {
    CN, L, ST, O, OU, C, STREET, DC, UID, T, DNQ, DNQUALIFIER, SURNAME,
    GIVENNAME, INITIALS, GENERATION, EMAILADDRESS, SERIALNUMBER;
}
