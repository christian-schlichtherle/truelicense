/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.core

import java.util.Date

import global.namespace.fun.io.api.Store
import global.namespace.fun.io.bios.BIOS.memory
import global.namespace.truelicense.api._
import global.namespace.truelicense.api.passwd.PasswordProtection
import global.namespace.truelicense.core.passwd.ObfuscatedPasswordProtection
import global.namespace.truelicense.obfuscate.ObfuscatedString
import global.namespace.truelicense.tests.core.TestContext._
import javax.security.auth.x500.X500Principal
import org.scalatest.Matchers._
import org.slf4j.LoggerFactory

trait TestContext {

  def managementContextBuilder: LicenseManagementContextBuilder

  protected def extra: AnyRef

  protected def prefix: String

  protected def postfix: String

  final lazy val vendorManager: VendorLicenseManager = {
    managementContext.vendor
      .encryption
      .protection(test1234)
      .up
      .authentication
      .alias("mykey")
      .loadFromResource(prefix + "private" + postfix)
      .storeProtection(test1234)
      .up
      .build
    }.ensuring(vm => vm.context eq managementContext)

  final lazy val chainedVendorManager: VendorLicenseManager = {
    managementContext.vendor
      .encryption
      .protection(test1234)
      .up
      .authentication
      .alias("mykey")
      .loadFromResource(prefix + "chained-private" + postfix)
      .storeProtection(test1234)
      .up
      .build
    }.ensuring(vm => vm.context eq managementContext)

  class State {

    final lazy val consumerStore: Store = memory

    final lazy val consumerManager: ConsumerLicenseManager = {
      managementContext.consumer
        .encryption
        .protection(test1234)
        .up
        .authentication
        .alias("mykey")
        .loadFromResource(prefix + "public" + postfix)
        .storeProtection(test1234)
        .up
        .storeIn(consumerStore)
        .build
      }.ensuring(cm => cm.context eq managementContext)

    final lazy val chainedConsumerStore: Store = memory

    final lazy val chainedConsumerManager: ConsumerLicenseManager = {
      managementContext.consumer
        .authentication
        .alias("mykey")
        .loadFromResource(prefix + "chained-public" + postfix)
        .storeProtection(test1234)
        .up
        .parent(consumerManager)
        .storeIn(chainedConsumerStore)
        .build
      }.ensuring(cm => cm.context eq managementContext)

    final lazy val ftpStore: Store = memory

    final val ftpManager: ConsumerLicenseManager = {
      managementContext.consumer
        .ftpDays(1)
        .authentication
        .alias("mykey")
        .loadFromResource(prefix + "ftp" + postfix)
        .storeProtection(test1234)
        .up
        .parent(consumerManager)
        .storeIn(ftpStore)
        .build
      }.ensuring(cm => cm.context eq managementContext)

    private lazy val licenseStore = {
      val s = memory
      vendorManager generateKeyFrom licenseBean saveTo s
      s
    }

    final def licenseKey: Array[Byte] = licenseStore.content
  }

  final def assertLicenseBean(license: License): Unit = {
    import license._
    getConsumerAmount shouldBe 1
    getConsumerType shouldBe "User"
    getExtra shouldBe extra
    getHolder shouldBe me
    getInfo shouldBe info
    getIssued shouldBe timestamp
    getIssuer shouldBe me
    getNotAfter shouldBe null
    getNotBefore shouldBe timestamp
    getSubject shouldBe managementContext.subject
  }

  final def licenseBean: License = {
    val l = managementContext.licenseFactory.license
    import l._
    setConsumerAmount(1)
    setConsumerType("User")
    setSubject(managementContext.subject)
    setHolder(me)
    setIssuer(me)
    setIssued(timestamp)
    setNotBefore(timestamp)
    setInfo(info)
    setExtra(extra)
    l
  }

  final lazy val managementContext: LicenseManagementContext = {
    managementContextBuilder
      .subject("subject")
      .validation(logger.debug("Validating license bean: {}", _))
      .build
  }
}

private object TestContext {

  private val info = "Hello, world!"

  private val logger = LoggerFactory getLogger classOf[TestContext]

  private val me = new X500Principal("CN=Christian Schlichtherle")

  private val test1234: PasswordProtection =
    new ObfuscatedPasswordProtection(new ObfuscatedString(Array[Long](0x545a955d0e30826cL, 0x3453ccaa499e6baeL))) /* => "test1234" */

  private val timestamp = new Date
}
