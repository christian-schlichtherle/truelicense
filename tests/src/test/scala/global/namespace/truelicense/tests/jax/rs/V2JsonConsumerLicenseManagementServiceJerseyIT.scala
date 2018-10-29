/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.jax.rs

import global.namespace.truelicense.tests.v2.json.V2JsonTestContext

/** @author Christian Schlichtherle */
class V2JsonConsumerLicenseManagementServiceJerseyIT
  extends ConsumerLicenseManagementServiceJerseyITSuite
    with V2JsonTestContext
    with NoExtraDataMixin
