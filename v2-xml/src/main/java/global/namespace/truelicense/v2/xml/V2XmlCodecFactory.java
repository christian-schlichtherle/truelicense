/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.xml;

import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.api.codec.CodecFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * A codec factory for use with V2/XML format license keys.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class V2XmlCodecFactory implements CodecFactory {

    public final Codec codec() {
        return new V2XmlCodec(this);
    }

    /**
     * Returns a new JAXB context.
     */
    public JAXBContext jaxbContext() {
        return jaxbContext(classesToBeBound());
    }

    /**
     * Returns a new JAXB context which binds the given classes.
     */
    protected final JAXBContext jaxbContext(final Class... classesToBeBound) {
        try {
            return JAXBContext.newInstance(classesToBeBound);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns the classes to be bound by the {@link #jaxbContext()}.
     */
    protected final Class<?>[] classesToBeBound() {
        return new Class[]{V2XmlLicense.class, V2XmlRepositoryModel.class};
    }

    /**
     * Configures the given marshaller which has been created by the {@link #jaxbContext()}.
     */
    protected void configure(Marshaller marshaller) {
    }

    /**
     * Configures the given unmarshaller which has been created by the {@link #jaxbContext()}.
     */
    protected void configure(Unmarshaller unmarshaller) {
    }
}
