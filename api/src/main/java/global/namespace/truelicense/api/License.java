/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.api.x500.X500PrincipalBuilder;

import javax.security.auth.x500.X500Principal;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;

/**
 * A value object which defines and provides the common properties of any license.
 * All properties are set to {@code null} by default.
 * However, {@linkplain LicenseValidation#validate license validation} may fail if some properties are {@code null} when
 * {@linkplain VendorLicenseManager#generateKeyFrom generating}, {@linkplain ConsumerLicenseManager#install installing}
 * or {@linkplain ConsumerLicenseManager#verify verifying} license keys.
 * <p>
 * There are two options for extending a license with custom properties:
 * <ul>
 * <li>The easiest way is to put the custom properties in a {@linkplain java.util.Map map} or another JavaBean and store
 * it in the {@linkplain #setExtra(Object) extra} property (composition).
 * <li>Another option is to subclass this class and add the custom properties (inheritance).
 * However, you may then need to override {@link #equals(Object)}, too.
 * If you do, you also need to override {@link #hashCode()} for consistency.
 * </ul>
 * Either way, the custom properties must be supported by the {@link Codec} of the respective license key format - see
 * {@link LicenseManagementContext#codec}.
 * This is easiest to achieve if the respective class follows the JavaBean pattern just like this interface does.
 *
 * @see LicenseFactory
 * @see LicenseManagementContextBuilder#licenseFactory
 */
@SuppressWarnings("unused")
public interface License {

    /**
     * Returns the amount of consumers which are allowed to use the
     * {@linkplain #getSubject() license management subject}.
     * The default value is one in order to retain compatibility with V1 format license keys and to reduce the size of
     * the encoded form (default values don't need to get encoded).
     */
    int getConsumerAmount();

    /**
     * Sets the amount of consumers which are allowed to use the {@linkplain #getSubject() license management subject}.
     */
    void setConsumerAmount(int consumerAmount);

    /**
     * Returns the description of the type of the entity or object which allocates (consumes) the license for using the
     * {@linkplain #getSubject() license management subject}.
     * This could describe a computer or a user or anything else, e.g. {@code "User"}.
     */
    String getConsumerType();

    /**
     * Sets the description of the type of the entity or object which allocates (consumes) the license for using the
     * {@linkplain #getSubject() license management subject}.
     */
    void setConsumerType(String consumerType);

    /**
     * Returns the license extra data.
     * Unlike the other properties of this class, this property is supposed to hold private information which should
     * never be shared with the customer.
     * It is typically used by applications in order to provide some extra data for
     * {@linkplain LicenseManagementContextBuilder#validation custom license validation}.
     */
    Object getExtra();

    /**
     * Sets the license extra data.
     *
     * @param extra the license extra data.
     *              This may be any object which is supported by the configured
     *              {@linkplain LicenseManagementContext#codec codec}.
     */
    void setExtra(Object extra);

    /**
     * Returns the distinguished name of the legal entity to which the license is granted by the issuer/vendor,
     * typically the consumer.
     * This could describe a person or an organization or be a placeholder for any of these, e.g.
     * {@code new X500Principal("CN=Unknown")}.
     */
    X500Principal getHolder();

    /**
     * Sets the distinguished name of the legal entity to which the license is granted by the issuer/vendor, typically
     * the consumer.
     *
     * @see X500PrincipalBuilder
     */
    void setHolder(X500Principal holder);

    /**
     * Returns the license information.
     * This could be any text which may be displayed to users for informational purposes.
     */
    String getInfo();

    /**
     * Sets the license information.
     */
    void setInfo(String info);

    /**
     * Returns the date/time when the license has been issued.
     * Note that a protective copy is made.
     */
    Date getIssued();

    /**
     * Sets the date/time when the license has been issued.
     * Note that a protective copy is made.
     */
    void setIssued(Date issued);

    /**
     * Returns the distinguished name of the legal entity which grants the
     * license to the holder/consumer, typically the license vendor.
     * This could describe a person or an organization or be a placeholder for any of these, e.g.
     * {@code new X500Principal("CN=Christian Schlichtherle,O=Schlichtherle IT Services")}.
     */
    X500Principal getIssuer();

    /**
     * Sets the distinguished name of the legal entity which grants the license to the holder/consumer, typically the
     * license vendor.
     *
     * @see X500PrincipalBuilder
     */
    void setIssuer(X500Principal issuer);

    /**
     * Returns the date/time when the license ends to be valid (expires).
     * Note that a protective copy is made.
     */
    Date getNotAfter();

    /**
     * Sets the date/time when the license ends to be valid (expires).
     * Note that a protective copy is made.
     */
    void setNotAfter(Date notAfter);

    /**
     * Returns the date/time when the license begins to be valid.
     * Note that a protective copy is made.
     */
    Date getNotBefore();

    /**
     * Sets the date/time when the license begins to be valid.
     * Note that a protective copy is made.
     */
    void setNotBefore(Date notBefore);

    /**
     * Conveniently computes the {@code issued} date/time, {@code notBefore} date/time and {@code notAfter} date/time
     * properties from the given number of days.
     * <p>
     * Note that this computation depends on the system clock unless the property {@code issued} is set.
     * This is not a security issue because this method is neither used for license initialization nor validation by a
     * consumer license manager.
     *
     * @param days the validity period in (24 hour) days from now.
     */
    default void setTerm(final int days) {
        Date issued = getIssued();
        if (null == issued) {
            issued = new Date();
            setIssued(issued);
        }
        setNotBefore(issued);
        final Calendar cal = getInstance();
        cal.setTime(issued);
        cal.add(DATE, days);
        setNotAfter(cal.getTime());
    }

    /**
     * Returns the license management subject.
     * This could be any string, but is recommended to contain the canonical name of the application and some arbitrary
     * pattern for the version number range this license is applicable, e.g. {@code "MyApp 1.X"}.
     */
    String getSubject();

    /**
     * Sets the license management subject.
     */
    void setSubject(String subject);

    /**
     * Returns {@code true} if and only if {@code obj} is an instance of the {@link License class} and its properties
     * compare {@linkplain Object#equals(Object) equal} to the properties of this license.
     */
    @Override
    boolean equals(final Object obj);

    /**
     * Returns {@code true} if and only if the given object can equal this object.
     * Only override this method if you want to constrain the {@link #equals} relation to a more specific type than this
     * interface.
     */
    default boolean canEqual(Object that) {
        return that instanceof License;
    }

    /**
     * Returns a hash code which is consistent with {@link #equals(Object)}.
     */
    @Override
    int hashCode();

    /**
     * Returns a string representation of this object for logging and debugging
     * purposes.
     */
    @Override
    String toString();
}
