/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.auth;

import org.truelicense.api.auth.Repository;
import org.truelicense.core.misc.Strings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

import static java.util.Locale.ENGLISH;

/**
 * A model for storing authenticated objects (artifacts) as encoded strings.
 * The content type and the content transfer encoding of the encoded artifact
 * and encoded signature are defined externally.
 * <p>
 * This class is used by {@linkplain Repository repositories} to store
 * artifacts for {@linkplain Repository#sign signing} and
 * {@linkplain Repository#verify verifying}.
 *
 * @author Christian Schlichtherle
 */
@ParametersAreNullableByDefault
@Nullable
public class RepositoryModel {

    private String artifact, signature, algorithm;

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

    /** Returns the signature algorithm. */
    @XmlElement(required = true)
    public final String getAlgorithm() { return algorithm; }

    /** Sets the signature algorithm. */
    public final void setAlgorithm(final String signatureAlgorithm) {
        this.algorithm = signatureAlgorithm;
    }

    /**
     * Returns {@code true} if and only if the given object is an instance of
     * {@code RepositoryModel} and it's encoded artifact, encoded signature and
     * signature algorithm compare equal, whereby the case of the signature
     * algorithm is ignored.
     */
    @Override
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RepositoryModel)) return false;
        final RepositoryModel that = (RepositoryModel) obj;
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
