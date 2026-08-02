/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.proguard;

/**
 * Input for {@link ProGuardTaskSpec}: {@code main} is kept, so ProGuard must rename {@code greeting} and nothing else.
 */
public final class ProGuardFixture {

    private String greeting() {
        return "hello";
    }

    public static void main(String[] args) {
        System.out.println(new ProGuardFixture().greeting());
    }
}
