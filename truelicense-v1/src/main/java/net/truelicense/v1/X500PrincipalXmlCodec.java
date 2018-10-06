/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.v1;

import javax.security.auth.x500.X500Principal;
import java.beans.DefaultPersistenceDelegate;
import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.OutputStream;

/**
 * A custom XML codec which adds a custom persistence delegate to each new
 * {@link XMLEncoder} for use with {@link X500Principal}s in the object graph.
 * This type of codec is used for V1 format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class X500PrincipalXmlCodec extends XmlCodec {

    private static final PersistenceDelegate delegate = new DefaultPersistenceDelegate(new String[]{ "name" });

    @Override
    XMLEncoder encoder(final OutputStream out) {
        final XMLEncoder enc = super.encoder(out);
        enc.setPersistenceDelegate(X500Principal.class, delegate);
        return enc;
    }
}
