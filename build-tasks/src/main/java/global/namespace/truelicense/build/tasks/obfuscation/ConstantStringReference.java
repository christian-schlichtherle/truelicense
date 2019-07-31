/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.obfuscation;

import static org.objectweb.asm.Opcodes.*;

final class ConstantStringReference {

    final int access;
    final String name, value;

    ConstantStringReference(final int access, final String name, final String value) {
        this.access = access | ACC_SYNTHETIC;
        this.name = name;
        this.value = value;
    }

    boolean keepField() {
        return isProtectedOrPublic() || !isStatic();
    }

    boolean isPrivate() {
        return 0 != (access & ACC_PRIVATE);
    }

    boolean isProtectedOrPublic() {
        return 0 != (access & (ACC_PROTECTED | ACC_PUBLIC));
    }

    boolean isStatic() {
        return 0 != (access & ACC_STATIC);
    }
}
