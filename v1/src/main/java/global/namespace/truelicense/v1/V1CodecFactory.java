/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v1;

import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.api.codec.CodecFactory;

import javax.security.auth.x500.X500Principal;
import java.beans.DefaultPersistenceDelegate;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A codec factory for use with V1 format license keys.
 */
@SuppressWarnings("WeakerAccess")
public class V1CodecFactory implements CodecFactory {

    private static final PersistenceDelegate delegate = new DefaultPersistenceDelegate(new String[]{"name"});

    public final Codec codec() {
        return new V1Codec(this);
    }

    /**
     * Returns a new XML encoder.
     */
    public XMLEncoder xmlEncoder(OutputStream out) {
        return configure(new XMLEncoder(out));
    }

    /**
     * Configures and returns the given XML encoder.
     */
    protected final XMLEncoder configure(XMLEncoder encoder) {
        encoder.setPersistenceDelegate(X500Principal.class, delegate);
        return encoder;
    }

    /**
     * Returns a new XML decoder.
     */
    public XMLDecoder xmlDecoder(InputStream in) {
        return configure(new XMLDecoder(in));
    }

    /**
     * Configures and returns the given XML decoder.
     */
    protected final XMLDecoder configure(XMLDecoder decoder) {
        return decoder;
    }
}
