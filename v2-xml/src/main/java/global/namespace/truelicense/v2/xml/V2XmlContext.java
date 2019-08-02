/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.xml;

import global.namespace.fun.io.api.function.XFunction;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * A context for use with V2/XML format license keys.
 */
@SuppressWarnings("WeakerAccess")
public class V2XmlContext {

    final V2XmlCodec codec() {
        return new V2XmlCodec(this);
    }

    /**
     * Returns a new JAXB context.
     */
    public JAXBContext jaxbContext() {
        return jaxbContext(JAXBContext::newInstance);
    }

    /**
     * Returns a new JAXB context obtained from the given factory.
     */
    public JAXBContext jaxbContext(JAXBContextFactory factory) {
        try {
            return factory.apply(new Class[]{V2XmlLicense.class, V2XmlRepositoryModel.class});
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Configures the given marshaller which has been created by the {@link #jaxbContext()}.
     */
    public void configure(Marshaller marshaller) {
    }

    /**
     * Configures the given unmarshaller which has been created by the {@link #jaxbContext()}.
     */
    public void configure(Unmarshaller unmarshaller) {
    }

    public interface JAXBContextFactory extends XFunction<Class[], JAXBContext> {
    }
}
