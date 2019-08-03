/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core;

import java.util.Objects;

public class ExtraBean {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ExtraBean)) {
            return false;
        }
        final ExtraBean that = (ExtraBean) obj;
        return Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message);
    }

    @Override
    public String toString() {
        return "ExtraBean{" +
                "message='" + message + '\'' +
                '}';
    }
}
