package net.truelicense.it.jax.rs

import global.namespace.fun.io.bios.BIOS.memory
import net.truelicense.api.License
import net.truelicense.it.core.TestContext
import net.truelicense.jax.rs.ConsumerLicenseManagementService

import scala.language.existentials

trait ConsumerLicenseManagementServiceITMixin { this: TestContext =>

  lazy val managementService: ConsumerLicenseManagementService = new ConsumerLicenseManagementService(consumerManager())

  protected lazy val (cachedLicenseClass, cachedLicenseBean, cachedLicenseKey): (Class[_ <: License], License, Array[Byte]) = {
    val store = memory
    val license = this.license
    val generator = vendorManager generateKeyFrom license saveTo store
    (license.getClass, generator.license, store.content)
  }
}
