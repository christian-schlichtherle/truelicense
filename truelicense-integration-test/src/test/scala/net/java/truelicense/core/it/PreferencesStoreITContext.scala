/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core.it

import java.util.prefs.Preferences
import net.java.truelicense.core.TestContext
import net.java.truelicense.core.io.PreferencesStore

/** @author Christian Schlichtherle */
trait PreferencesStoreITContext { this: TestContext =>
  override final def store = {
    val prefs = Preferences userNodeForPackage getClass
    new PreferencesStore(prefs)
  }
}
