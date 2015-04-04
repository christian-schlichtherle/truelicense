package net.java.truelicense.it.jax.rs

import net.java.truelicense.core.io.MemoryStore
import net.java.truelicense.core.{License, LicenseVendorManager}

/**
 * A simple bean which holds a license key and its encoded license bean.
 * Note that the key and the bean get shared with the caller.
 */
class LicenseBeanAndKeyHolder(vm: LicenseVendorManager, _bean: License) {
  private val store = new MemoryStore
  val bean = vm.create(_bean, store)
  val key = store.data
}
