/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.xml;

import global.namespace.truelicense.core.LicenseStub;

import javax.security.auth.x500.X500Principal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * Provides compatibility with V2/XML format license keys.
 */
// As of Java 8, JAXB doesn't properly handle super classes, e.g. the @XmlJavaTypeAdapter annotation would be ignored
// here, so we need to copy/paste the code from `global.namespace.truelicense.core.AbstractLicense` rather than inherit
// from it.

// #TRUELICENSE-50: The XML root element name MUST be explicitly defined!
// Otherwise, it would get derived from the class name, but this would break if the class name gets obfuscated, e.g.
// when using the ProGuard configuration from the TrueLicense Maven Archetype.
@XmlRootElement(name = "license")

// #TRUELICENSE-52: Dito for the XML type.
// This annotation enables objects of this class to participate in larger object graphs which the application wants to
// encode/decode to/from XML.
@XmlType(name = "license")

public class V2XmlLicense extends LicenseStub {

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

    @Override
    @XmlElement(defaultValue = "1")
    public int getConsumerAmount() {
        return consumerAmount;
    }

    @Override
    public void setConsumerAmount(final int consumerAmount) {
        this.consumerAmount = consumerAmount;
    }

    @Override
    @XmlElement(required = true)
    public String getConsumerType() {
        return consumerType;
    }

    @Override
    public void setConsumerType(final String consumerType) {
        this.consumerType = consumerType;
    }

    @Override
    public Object getExtra() {
        return extra;
    }

    @Override
    public void setExtra(final Object extra) {
        this.extra = extra;
    }

    @Override
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(X500PrincipalXmlAdapter.class)
    public X500Principal getHolder() {
        return holder;
    }

    @Override
    public void setHolder(final X500Principal holder) {
        this.holder = holder;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(final String info) {
        this.info = info;
    }

    @Override
    @XmlElement(required = true)
    public Date getIssued() {
        return clone(issued);
    }

    @Override
    public void setIssued(final Date issued) {
        this.issued = clone(issued);
    }

    @Override
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(X500PrincipalXmlAdapter.class)
    public X500Principal getIssuer() {
        return issuer;
    }

    @Override
    public void setIssuer(final X500Principal issuer) {
        this.issuer = issuer;
    }

    @Override
    public Date getNotAfter() {
        return clone(notAfter);
    }

    @Override
    public void setNotAfter(final Date notAfter) {
        this.notAfter = clone(notAfter);
    }

    @Override
    public Date getNotBefore() {
        return clone(notBefore);
    }

    @Override
    public void setNotBefore(final Date notBefore) {
        this.notBefore = clone(notBefore);
    }

    @Override
    @XmlElement(required = true)
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    private static Date clone(Date date) {
        return null == date ? null : (Date) date.clone();
    }
}
