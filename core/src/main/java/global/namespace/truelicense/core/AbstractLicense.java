/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.core;

import javax.security.auth.x500.X500Principal;
import java.util.Date;

/**
 * A base class for any license.
 */
public abstract class AbstractLicense extends LicenseStub {

    private int consumerAmount = 1; // default value is required for compatibility with V1 license keys.
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
    public int getConsumerAmount() {
        return consumerAmount;
    }

    @Override
    public void setConsumerAmount(final int consumerAmount) {
        this.consumerAmount = consumerAmount;
    }

    @Override
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
    public Date getIssued() {
        return clone(issued);
    }

    @Override
    public void setIssued(final Date issued) {
        this.issued = clone(issued);
    }

    @Override
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
