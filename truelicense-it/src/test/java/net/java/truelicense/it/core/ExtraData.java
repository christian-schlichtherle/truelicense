/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.core;

import net.java.truelicense.core.util.Objects;

public class ExtraData {

    private String message;

    public String getMessage() { return message; }

    public void setMessage(final String message) { this.message = message; }

    @Override public boolean equals(final Object other) {
        if (!(other instanceof ExtraData)) return false;
        final ExtraData that = (ExtraData) other;
        return Objects.equals(this.message, that.message);
    }

    @Override public int hashCode() { return Objects.hashCode(message); }
}