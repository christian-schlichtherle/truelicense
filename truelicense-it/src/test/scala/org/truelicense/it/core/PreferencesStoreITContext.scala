/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

/** @author Christian Schlichtherle */
trait PreferencesStoreITContext { this: TestContext =>
  override final def store = vendorContext userPreferencesStore getClass
}
