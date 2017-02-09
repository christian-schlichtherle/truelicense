/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package ${package}.keymgrsvc;

import ${package}.keymgr.LicenseManager;
import javax.ws.rs.ext.*;
import org.truelicense.api.ConsumerLicenseManager;

/**
 * @author Christian Schlichtherle
 */
@Provider
public final class ConsumerLicenseManagerResolver
implements ContextResolver<ConsumerLicenseManager> {
    @Override public ConsumerLicenseManager getContext(Class<?> ignored) {
        return LicenseManager.get();
    }
}
