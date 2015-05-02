/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v1.codec;

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
public class X500PrincipalXmlCodec extends XmlCodec {

    private static final PersistenceDelegate pd4xp =
            new DefaultPersistenceDelegate(new String[]{ "name" }); // NOI18N

    @Override
    protected XMLEncoder encoder(final OutputStream out) {
        final XMLEncoder enc = super.encoder(out);
        enc.setPersistenceDelegate(X500Principal.class, pd4xp);
        return enc;
    }
}
