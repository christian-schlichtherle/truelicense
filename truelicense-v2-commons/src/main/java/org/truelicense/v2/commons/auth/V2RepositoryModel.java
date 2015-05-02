/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v2.commons.auth;

import org.truelicense.api.auth.RepositoryModel;
import org.truelicense.core.misc.Strings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

import static java.util.Locale.ENGLISH;

/**
 * A repository model for use with V2 format license keys.
 * All properties are set to {@code null} by default.
 *
 * @author Christian Schlichtherle
 */
// #TRUELICENSE-50: The XML element name MUST be explicitly defined!
// Otherwise, it would get derived from the class name, but this would break
// if the class name gets obfuscated, e.g. when using the ProGuard
// configuration from the TrueLicense Maven Archetype.
@XmlRootElement(name = "repository")
// #TRUELICENSE-52: Dito for the XML type. This enables objects of this class
// to participate in larger object graphs which the application wants to
// encode/decode to/from XML.
@XmlType(name = "repository")
public final class V2RepositoryModel implements RepositoryModel {

    private String algorithm, artifact, signature;

    /** Returns the signature algorithm. */
    @XmlElement(required = true)
    public final String getAlgorithm() { return algorithm; }

    /** Sets the signature algorithm. */
    public final void setAlgorithm(final String signatureAlgorithm) {
        this.algorithm = signatureAlgorithm;
    }

    /** Returns the encoded artifact. */
    @XmlElement(required = true)
    public final String getArtifact() { return artifact; }

    /** Sets the encoded artifact. */
    public final void setArtifact(final String encodedArtifact) {
        this.artifact = encodedArtifact;
    }

    /** Returns the encoded signature. */
    @XmlElement(required = true)
    public final String getSignature() { return signature; }

    /** Sets the encoded signature. */
    public void setSignature(final String encodedSignature) {
        this.signature = encodedSignature;
    }

    /**
     * Returns {@code true} if and only if the given object is an instance of
     * {@link V2RepositoryModel} and it's signature algorithm, encoded
     * artifact and encoded signature compare equal, whereby the case of the
     * signature algorithm is ignored.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof V2RepositoryModel)) return false;
        final V2RepositoryModel that = (V2RepositoryModel) obj;
        return  Objects.equals(this.getArtifact(), that.getArtifact()) &&
                Objects.equals(this.getSignature(), that.getSignature()) &&
                Strings.equalsIgnoreCase(this.getAlgorithm(), that.getAlgorithm());
    }

    /** Returns a hash code which is consistent with {@link #equals}. */
    @Override
    public int hashCode() {
        int c = 17;
        c = 31 * c + Objects.hashCode(getArtifact());
        c = 31 * c + Objects.hashCode(getSignature());
        c = 31 * c + Objects.hashCode(Strings.toLowerCase(getAlgorithm(), ENGLISH));
        return c;
    }
}
