/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.x500;

/**
 * @author Christian Schlichtherle
 */
class MyX500PrincipalBuilder
extends GenericX500PrincipalBuilder<MyX500PrincipalBuilder> {
    MyX500PrincipalBuilder addFOO(String value) {
        addKeyword("FOO", "1.2.3.4.5.6.7.8.9");
        return addAttribute("FOO", value);
    }
}
