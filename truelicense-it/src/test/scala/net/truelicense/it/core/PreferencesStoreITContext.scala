/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import net.truelicense.api.io.Store

/** @author Christian Schlichtherle */
trait PreferencesStoreITContext { this: TestContext[_] =>
  final override def store: Store = managementContext userPreferencesStore getClass
}
