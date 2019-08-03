/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.v1

import global.namespace.truelicense.tests.core.NoExtraDataTestContext
import global.namespace.truelicense.tests.jax.rs.ConsumerLicenseManagementServiceJerseyITLike

class V1ConsumerLicenseManagementServiceJerseyIT
  extends ConsumerLicenseManagementServiceJerseyITLike
    with V1TestContext
    with NoExtraDataTestContext
