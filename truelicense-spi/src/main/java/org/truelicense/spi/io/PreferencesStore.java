/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.io;

import org.truelicense.api.io.Store;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static java.util.Objects.requireNonNull;

/**
 * A preferences (node) store.
 *
 * @author Christian Schlichtherle
 */
final class PreferencesStore implements Store {

    private final Preferences prefs;
    private final String key;

    PreferencesStore(final Preferences prefs, final String key) {
        this.prefs = requireNonNull(prefs);
        this.key = requireNonNull(key);
    }

    public boolean isUserNode() { return prefs.isUserNode(); }

    private byte[] checkedData() throws FileNotFoundException {
        final byte[] data = data();
        if (null == data)
            throw new FileNotFoundException(
                    "Cannot locate the key \"" + key + "\" in the " +
                    (isUserNode() ? "user" : "system") +
                    " preferences node for the absolute path \"" +
                    prefs.absolutePath() + "\".");
        return data;
    }

    public @Nullable byte[] data() { return prefs.getByteArray(key, null); }

    public void data(@CheckForNull byte[] data) {
        if (null != data) prefs.putByteArray(key, data);
        else prefs.remove(key);
    }

    @Override public InputStream input() throws IOException {
        return new ByteArrayInputStream(checkedData());
    }

    @Override public OutputStream output() throws IOException {
        return new ByteArrayOutputStream(BUFSIZE) {
            @Override
            public void close() throws IOException {
                data(toByteArray());
                sync();
            }
        };
    }

    @Override public void delete() throws IOException {
        checkedData();
        data(null);
        sync();
    }

    private void sync() throws IOException {
        try { prefs.flush(); }
        catch (final BackingStoreException ex) { throw new IOException(ex); }
    }

    @Override public boolean exists() { return null != data(); }
}
