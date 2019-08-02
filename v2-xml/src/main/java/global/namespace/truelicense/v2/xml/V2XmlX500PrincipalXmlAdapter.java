/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.xml;

import javax.security.auth.x500.X500Principal;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * An XML adapter for use with {@link X500Principal}s in V2/XML format license keys.
 */
final class V2XmlX500PrincipalXmlAdapter extends XmlAdapter<String, X500Principal> {

    @Override
    public X500Principal unmarshal(String name) {
        return new X500Principal(name);
    }

    @Override
    public String marshal(X500Principal principal) {
        return principal.getName();
    }
}
