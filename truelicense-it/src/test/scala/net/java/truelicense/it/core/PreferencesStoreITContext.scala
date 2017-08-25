/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.core

import java.util.prefs.Preferences

import net.java.truelicense.core.io.{PreferencesStore, Store}

/** @author Christian Schlichtherle */
trait PreferencesStoreITContext { this: TestContext =>
  override final def store: Store = {
    val prefs = Preferences userNodeForPackage getClass
    new PreferencesStore(prefs)
  }
}
