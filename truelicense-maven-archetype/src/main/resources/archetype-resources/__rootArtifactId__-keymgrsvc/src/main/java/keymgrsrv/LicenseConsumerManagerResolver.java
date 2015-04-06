/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package ${package}.keymgrsvc;

import ${package}.keymgr.LicenseManager;
import javax.ws.rs.ext.*;
import org.truelicense.api.LicenseConsumerManager;

/**
 * @author Christian Schlichtherle
 */
@Provider
public final class LicenseConsumerManagerResolver
implements ContextResolver<LicenseConsumerManager> {
    @Override public LicenseConsumerManager getContext(Class<?> ignored) {
        return LicenseManager.get();
    }
}
