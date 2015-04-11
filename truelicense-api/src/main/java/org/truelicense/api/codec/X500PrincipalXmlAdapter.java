/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.codec;

import javax.annotation.concurrent.Immutable;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * An XML adapter for {@link X500Principal}s for use with JAXB.
 * <p>
 * This type of XML adapter is used for V2/XML format license keys.
 *
 * @author Christian Schlichtherle
 */
// TODO: Maybe make this class package private and move it one package level up?
@Immutable
public final class X500PrincipalXmlAdapter
extends XmlAdapter<String, X500Principal> {
    @Override public X500Principal unmarshal(String name) {
        return new X500Principal(name);
    }

    @Override public String marshal(X500Principal principal) {
        return principal.getName();
    }
}
