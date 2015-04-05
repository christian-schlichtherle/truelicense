/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.util.prefs.Preferences

import org.truelicense.core.io.PreferencesStore

/** @author Christian Schlichtherle */
trait PreferencesStoreITContext { this: TestContext =>
  override final def store = {
    val prefs = Preferences userNodeForPackage getClass
    new PreferencesStore(prefs)
  }
}
