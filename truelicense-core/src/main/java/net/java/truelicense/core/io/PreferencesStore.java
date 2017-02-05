/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import static net.java.truelicense.core.util.Objects.*;
import net.java.truelicense.obfuscate.Obfuscate;

/**
 * A preferences (node) store.
 *
 * @author Christian Schlichtherle
 */
public final class PreferencesStore implements Store {

    /** The name of the key for the preferences node where the data is stored. */
    @Obfuscate
    private static final String PREFERENCES_KEY = "key";

    private final Preferences prefs;
    private final String key;

    public PreferencesStore(Preferences preferences) {
        this(preferences, PREFERENCES_KEY);
    }

    public PreferencesStore(final Preferences prefs, final String key) {
        this.prefs = requireNonNull(prefs);
        this.key = requireNonNull(key);
    }

    @Override
    public InputStream input() throws IOException {
        return new ByteArrayInputStream(checkedData());
    }

    @Override
    public OutputStream output() throws IOException {
        return new ByteArrayOutputStream(BUFSIZE) {
            @Override
            public void close() throws IOException {
                data(toByteArray());
                sync();
            }
        };
    }

    @Override
    public void delete() throws IOException {
        checkedData();
        data(null);
        sync();
    }

    @Override
    public boolean exists() { return null != data(); }

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

    public boolean isUserNode() { return prefs.isUserNode(); }

    public void sync() throws IOException {
        try { prefs.flush(); }
        catch (BackingStoreException e) { throw new IOException(e); }
    }
}
