/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.jax.rs

import net.truelicense.it.core.TestContext

trait NoExtraDataMixin { this: TestContext =>

  final override def extraData: AnyRef = null
}
