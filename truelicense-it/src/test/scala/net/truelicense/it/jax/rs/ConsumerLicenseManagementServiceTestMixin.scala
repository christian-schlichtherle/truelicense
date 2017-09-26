package net.truelicense.it.jax.rs

import net.truelicense.api.License
import net.truelicense.it.core.TestContext
import net.truelicense.jax.rs.ConsumerLicenseManagementService
import net.truelicense.spi.io.MemoryStore
import scala.language.existentials

trait ConsumerLicenseManagementServiceTestMixin {
  this: TestContext[_] =>

  lazy val managementService: ConsumerLicenseManagementService = {
    lazy val m = consumerManager()
    new ConsumerLicenseManagementService(() => m)
  }

  protected lazy val (cachedLicenseClass, cachedLicenseBean, cachedLicenseKey): (Class[_ <: License], License, Array[Byte]) = {
    val store = new MemoryStore
    val bean = license
    (bean.getClass, (vendorManager generateKeyFrom bean saveTo store).license, store.data)
  }
}
