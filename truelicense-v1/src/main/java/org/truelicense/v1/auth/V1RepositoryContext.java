package org.truelicense.v1.auth;

import de.schlichtherle.xml.GenericCertificate;
import org.truelicense.api.auth.Repository;
import org.truelicense.api.auth.RepositoryContext;
import org.truelicense.api.codec.Codec;

/**
 * A repository context for use with V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
public final class V1RepositoryContext
implements RepositoryContext<GenericCertificate> {

    @Override
    public GenericCertificate model() {
        return new GenericCertificate();
    }

    @Override
    public Repository controller(GenericCertificate model, Codec codec) {
        return new V1RepositoryController(model, codec);
    }
}
