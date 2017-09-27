/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.codec.Codec;
import net.truelicense.api.codec.X500PrincipalXmlAdapter;

import javax.security.auth.x500.X500Principal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static java.util.Calendar.DATE;
import static java.util.Calendar.getInstance;

/**
 * A Java Bean which defines and provides the common properties of any license.
 * All properties are set to {@code null} by default.
 * However, {@linkplain LicenseValidation#validate license validation} may fail
 * if some properties are {@code null} when
 * {@linkplain VendorLicenseManager#generateKeyFrom generating},
 * {@linkplain ConsumerLicenseManager#install installing} or
 * {@linkplain ConsumerLicenseManager#verify verifying}
 * license keys.
 * <p>
 * There are two options for extending a license with custom properties:
 * <ul>
 * <li>The easiest way is to put the custom properties in a
 *     {@linkplain java.util.Map map} or another JavaBean and store it in the
 *     {@linkplain #setExtra(Object) extra} property (composition).
 * <li>Another option is to subclass this class and add the custom properties
 *     (inheritance).
 *     However, you may then need to override {@link #equals(Object)}, too.
 *     If you do, you also need to override {@link #hashCode()} for consistency.
 * </ul>
 * Either way, the custom properties must support the {@link Codec} of the
 * respective license key format - see {@link LicenseManagementContext#codec}.
 * This is easiest to achieve if the respective class follows the JavaBean
 * pattern just like this class does.
 * <p>
 * Note that this class deviates from the JavaBeans specification in that it
 * neither implements {@link java.io.Serializable} nor
 * {@link java.io.Externalizable}.
 * This is because object serialization is not required with the used
 * {@code Codec}s and creates a major obligation for any subclass.
 *
 * @see    LicenseFactory
 * @author Christian Schlichtherle
 */
// #TRUELICENSE-50: The XML root element name MUST be explicitly defined!
// Otherwise, it would get derived from the class name, but this would break
// if the class name gets obfuscated, e.g. when using the ProGuard
// configuration from the TrueLicense Maven Archetype.
@XmlRootElement(name = "license")
// #TRUELICENSE-52: Dito for the XML type. This enables objects of this class
// to participate in larger object graphs which the application wants to
// encode/decode to/from XML.
@XmlType(name = "license")
public class License {

    private int consumerAmount = 1;
    private String consumerType;
    private Object extra;
    private X500Principal holder;
    private String info;
    private Date issued;
    private X500Principal issuer;
    private Date notAfter;
    private Date notBefore;
    private String subject;

    /**
     * Returns the amount of consumers which are allowed to use the
     * {@linkplain #getSubject() license management subject}.
     * The default value is one in order to retain compatibility with V1
     * format license keys and to reduce the size of the XML encoded form
     * (default values don't need to get encoded).
     */
    @XmlElement(defaultValue = "1")
    public final int getConsumerAmount() { return consumerAmount; }

    /**
     * Sets the amount of consumers which are allowed to use the
     * {@linkplain #getSubject() license management subject}.
     */
    public final void setConsumerAmount(final int consumerAmount) {
        this.consumerAmount = consumerAmount;
    }

    /**
     * Returns the description of the type of the entity or object which
     * allocates (consumes) the license for using the
     * {@linkplain #getSubject() license management subject}.
     * This could describe a computer or a user or anything else, e.g.
     * {@code "User"}.
     */
    @XmlElement(required = true)
    public final String getConsumerType() { return consumerType; }

    /**
     * Sets the description of the type of the entity or object which
     * allocates (consumes) the license for using the
     * {@linkplain #getSubject() license management subject}.
     */
    public final void setConsumerType(final String consumerType) {
        this.consumerType = consumerType;
    }

    /**
     * Returns the license extra data.
     * Unlike the other properties of this class, this property is supposed to
     * hold private information which should never be shared with the customer.
     * It is typically used by applications in order to provide some extra data
     * for
     * {@linkplain LicenseManagementContextBuilder#validation custom license validation}.
     */
    public final Object getExtra() { return extra; }

    /**
     * Sets the license extra data.
     *
     * @param extra the license extra data.
     *              This may be any object which is supported by the configured
     *              {@linkplain LicenseManagementContext#codec codec}.
     */
    public final void setExtra(final Object extra) {
        this.extra = extra;
    }

    /**
     * Returns the distinguished name of the legal entity to which the license
     * is granted by the issuer/vendor, typically the consumer.
     * This could describe a person or an organization or be a placeholder for
     * any of these, e.g. {@code new X500Principal("CN=Unknown")}.
     */
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(X500PrincipalXmlAdapter.class)
    public final X500Principal getHolder() { return holder; }

    /**
     * Sets the distinguished name of the legal entity to which the license
     * is granted by the issuer/vendor, typically the consumer.
     *
     * @see X500PrincipalBuilder
     */
    public final void setHolder(final X500Principal holder) {
        this.holder = holder;
    }

    /**
     * Returns the license information.
     * This could be any text which may be displayed to users for informational
     * purposes.
     */
    public final String getInfo() { return info; }

    /**
     * Sets the license information.
     */
    public final void setInfo(final String info) {
        this.info = info;
    }

    /**
     * Returns the date/time when the license has been issued.
     * Note that a protective copy is made.
     */
    @XmlElement(required = true)
    public final Date getIssued() { return clone(issued); }

    /**
     * Sets the date/time when the license has been issued.
     * Note that a protective copy is made.
     */
    public final void setIssued(final Date issued) {
        this.issued = clone(issued);
    }

    /**
     * Returns the distinguished name of the legal entity which grants the
     * license to the holder/consumer, typically the license vendor.
     * This could describe a person or an organization or be a placeholder for
     * any of these, e.g.
     * {@code new X500Principal("CN=Christian Schlichtherle,O=Schlichtherle IT Services")}.
     */
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(X500PrincipalXmlAdapter.class)
    public final X500Principal getIssuer() { return issuer; }

    /**
     * Sets the distinguished name of the legal entity which grants the
     * license to the holder/consumer, typically the license vendor.
     *
     * @see X500PrincipalBuilder
     */
    public final void setIssuer(final X500Principal issuer) {
        this.issuer = issuer;
    }

    /**
     * Returns the date/time when the license ends to be valid (expires).
     * Note that a protective copy is made.
     */
    public final Date getNotAfter() { return clone(notAfter); }

    /**
     * Sets the date/time when the license ends to be valid (expires).
     * Note that a protective copy is made.
     */
    public final void setNotAfter(final Date notAfter) {
        this.notAfter = clone(notAfter);
    }

    /**
     * Returns the date/time when the license begins to be valid.
     * Note that a protective copy is made.
     */
    public final Date getNotBefore() { return clone(notBefore); }

    /**
     * Sets the date/time when the license begins to be valid.
     * Note that a protective copy is made.
     */
    public final void setNotBefore(final Date notBefore) {
        this.notBefore = clone(notBefore);
    }

    /**
     * Conveniently computes the {@code issued} date/time, {@code notBefore}
     * date/time and {@code notAfter} date/time properties from the given
     * number of days.
     * <p>
     * Note that this computation depends on the system clock unless the
     * property {@code issued} is set.
     * This is not a security issue because this method is neither used for
     * license initialization nor validation by a consumer license manager.
     *
     * @param days the validity period in (24 hour) days from now.
     */
    public final void setTerm(final int days) {
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
     * This could be any string, but is recommended to contain the canonical
     * name of the application and some information for which version number
     * range this license is applicable, e.g. {@code "MyApp 1.X"}.
     */
    @XmlElement(required = true)
    public final String getSubject() { return subject; }

    /**
     * Sets the license management subject.
     */
    public final void setSubject(final String subject) {
        this.subject = subject;
    }

    private static Date clone(Date date) {
        return null == date ? null : (Date) date.clone();
    }

    /**
     * Returns {@code true} if and only if {@code obj} is an instance of the
     * {@link License class} and its properties compare
     * {@linkplain Object#equals(Object) equal} to the properties of this
     * license.
     */
    @Override
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof License))
            return false;
        final License that = (License) obj;
        return  this.getConsumerAmount() == that.getConsumerAmount()
                && Objects.equals(this.getConsumerType(), that.getConsumerType())
                && Objects.equals(this.getExtra(), that.getExtra())
                && Objects.equals(this.getHolder(), that.getHolder())
                && Objects.equals(this.getInfo(), that.getInfo())
                && Objects.equals(this.getIssued(), that.getIssued())
                && Objects.equals(this.getIssuer(), that.getIssuer())
                && Objects.equals(this.getNotAfter(), that.getNotAfter())
                && Objects.equals(this.getNotBefore(), that.getNotBefore())
                && Objects.equals(this.getSubject(), that.getSubject());
    }

    /** Returns a hash code which is consistent with {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        int c = 17;
        c = 31 * c + getConsumerAmount();
        c = 31 * c + Objects.hashCode(getConsumerType());
        c = 31 * c + Objects.hashCode(getExtra());
        c = 31 * c + Objects.hashCode(getHolder());
        c = 31 * c + Objects.hashCode(getInfo());
        c = 31 * c + Objects.hashCode(getIssued());
        c = 31 * c + Objects.hashCode(getIssuer());
        c = 31 * c + Objects.hashCode(getNotAfter());
        c = 31 * c + Objects.hashCode(getNotBefore());
        c = 31 * c + Objects.hashCode(getSubject());
        return c;
    }

    /**
     * Returns a string representation of this object for logging and debugging
     * purposes.
     */
    @Override
    public String toString() {
        return String.format("%s@%x[subject=%s, holder=%s, issuer=%s, issued=%tc, notBefore=%tc, notAfter=%tc, consumerType=%s, consumerAmount=%d, info=%s]",
                getClass().getName(),
                hashCode(),
                literal(getSubject()),
                literal(getHolder()),
                literal(getIssuer()),
                getIssued(),
                getNotBefore(),
                getNotAfter(),
                literal(getConsumerType()),
                getConsumerAmount(),
                literal(getInfo()));
    }

    private static String literal(final Object obj) {
        if (null == obj)
            return null;
        final String s = obj.toString();
        return '"' +
                s   .replace("\\", "\\\\")
                    .replace("\"", "\\\"") +
                '"';
    }
}
