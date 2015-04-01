package net.java.truelicense.ws.rs

import net.java.truelicense.core.{License, LicenseVendorManager}
import net.java.truelicense.core.io.MemoryStore

/**
 * A simple bean which holds a license key and its encoded license bean.
 * Note that the key and the bean get shared with the caller.
 */
class KeyWithLicenseHolder(vm: LicenseVendorManager, lic: License) {
  private val store = new MemoryStore
  val license = vm.create(lic, store)
  val key = store.data
}
