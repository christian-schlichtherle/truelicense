/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v1;

import global.namespace.fun.io.api.function.XFunction;
import global.namespace.truelicense.api.codec.Codec;

import javax.security.auth.x500.X500Principal;
import java.beans.DefaultPersistenceDelegate;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A context for use with V1 format license keys.
 */
@SuppressWarnings("WeakerAccess")
public class V1Context {

    private static final PersistenceDelegate delegate = new DefaultPersistenceDelegate(new String[]{"name"});

    final Codec codec() {
        return new V1Codec(this);
    }

    /**
     * Returns a new XML encoder factory.
     */
    public XMLEncoderFactory encoderFactory() {
        return encoderFactory(XMLEncoder::new);
    }

    /**
     * Decorates the given XML encoder factory for configuring the obtained encoder.
     */
    public XMLEncoderFactory encoderFactory(XMLEncoderFactory factory) {
        return out -> {
            final XMLEncoder enc = factory.apply(out);
            enc.setPersistenceDelegate(X500Principal.class, delegate);
            return enc;
        };
    }

    /**
     * Returns a new XML decoder factory.
     */
    public XMLDecoderFactory decoderFactory() {
        return decoderFactory(XMLDecoder::new);
    }

    /**
     * Decorates the given XML decoder factory for configuring the obtained decoder.
     */
    public XMLDecoderFactory decoderFactory(XMLDecoderFactory factory) {
        return factory;
    }

    public interface XMLEncoderFactory extends XFunction<OutputStream, XMLEncoder> {
    }

    public interface XMLDecoderFactory extends XFunction<InputStream, XMLDecoder> {
    }
}
