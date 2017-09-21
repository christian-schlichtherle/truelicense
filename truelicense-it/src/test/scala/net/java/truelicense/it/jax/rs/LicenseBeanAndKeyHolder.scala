/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.jax.rs

import net.java.truelicense.core.io.MemoryStore
import net.java.truelicense.core.{License, LicenseVendorManager}

/**
 * A simple bean which holds a license key and its encoded license bean.
 * Note that the key and the bean get shared with the caller.
 */
class LicenseBeanAndKeyHolder(vm: LicenseVendorManager, _bean: License) {
  val (bean: License, key: Array[Byte]) = {
    val store = new MemoryStore
    vm.create(_bean, store) -> store.data
  }
}
