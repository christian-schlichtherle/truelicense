/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.jax.rs

import global.namespace.fun.io.bios.BIOS.memory
import global.namespace.truelicense.api.License
import global.namespace.truelicense.jax.rs.ConsumerLicenseManagementService
import global.namespace.truelicense.tests.core.TestContext

import scala.language.existentials

trait ConsumerLicenseManagementServiceITMixin {
  this: TestContext =>

  lazy val managementService: ConsumerLicenseManagementService = new ConsumerLicenseManagementService(consumerManager())

  protected lazy val (cachedLicenseBean, cachedLicenseKey): (License, Array[Byte]) = {
    val store = memory
    val license = this.license
    val generator = vendorManager generateKeyFrom license saveTo store
    (generator.license, store.content)
  }
}
