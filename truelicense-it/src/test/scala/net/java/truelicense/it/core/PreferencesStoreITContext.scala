package net.java.truelicense.it.core

import java.util.prefs.Preferences

import net.java.truelicense.core.io.PreferencesStore

/** @author Christian Schlichtherle */
trait PreferencesStoreITContext { this: TestContext =>
  override final def store = {
    val prefs = Preferences userNodeForPackage getClass
    new PreferencesStore(prefs)
  }
}
