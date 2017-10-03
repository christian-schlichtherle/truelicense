/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.io;

import net.truelicense.api.io.Store;

import java.io.*;
import java.util.Optional;
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

    @Override
    public InputStream input() throws IOException {
        return new ByteArrayInputStream(data());
    }

    @Override
    public OutputStream output() throws IOException {
        return new ByteArrayOutputStream(BUFSIZE) {
            @Override
            public void close() throws IOException {
                data(toByteArray());
            }
        };
    }

    @Override
    public void delete() throws IOException {
        data();
        prefs.remove(key);
        sync();
    }

    @Override
    public boolean exists() { return optData().isPresent(); }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private byte[] data() throws IOException {
        return optData()
                .orElseThrow(() -> new FileNotFoundException(
                        "Cannot locate the key \"" + key + "\" in the " +
                                (prefs.isUserNode() ? "user" : "system") +
                                " preferences node for the absolute path \"" +
                                prefs.absolutePath() + "\"."));
    }

    private void data(byte[] data) throws IOException {
        prefs.putByteArray(key, data);
        sync();
    }

    private Optional<byte[]> optData() {
        return Optional.ofNullable(prefs.getByteArray(key, null));
    }

    private void sync() throws IOException {
        try { prefs.flush(); }
        catch (final BackingStoreException e) { throw new IOException(e); }
    }
}
