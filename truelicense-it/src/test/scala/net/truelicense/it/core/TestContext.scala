/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.util.Calendar._
import java.util.Date
import javax.security.auth.x500.X500Principal

import global.namespace.fun.io.api.Store
import global.namespace.fun.io.bios.BIOS.memoryStore
import net.truelicense.api._
import net.truelicense.api.auth.{RepositoryContext, RepositoryContextProvider}
import net.truelicense.api.codec.{Codec, CodecProvider}
import net.truelicense.api.passwd.PasswordProtection
import net.truelicense.core.TrueLicenseApplicationContext
import net.truelicense.core.passwd.ObfuscatedPasswordProtection
import net.truelicense.it.core.TestContext._
import net.truelicense.obfuscate.ObfuscatedString
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
trait TestContext[Model <: AnyRef]
  extends CodecProvider
  with RepositoryContextProvider[Model] {

  val applicationContext: TrueLicenseApplicationContext[Model]

  def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager

  def chainedVendorManager: VendorLicenseManager

  final override def codec: Codec = managementContext.codec

  final def consumerManager(): ConsumerLicenseManager = consumerManager(memoryStore)

  def consumerManager(store: Store): ConsumerLicenseManager

  final def datePlusDays(date: Date, days: Int): Date = {
    val cal = getInstance
    import cal._
    setTime(date)
    assert(isLenient)
    add(DATE, days)
    getTime
  }

  def extraData: AnyRef = { // must be AnyRef to enable overriding and returning a bean instead.

    // The XmlEncoder used with V1 format license keys supports only standard
    // Java collections by default, so I cannot use collection.JavaConverters
    // here because it would create a custom implementation.
    val map = new java.util.HashMap[String, String]
    map.put("message", "This is some private extra data!")
    map
  }

  def ftpConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager

  final def license: License = {
    val now = new Date
    val me = new X500Principal("CN=Christian Schlichtherle")
    val license = managementContext.license
    import license._
    setSubject("subject")
    setHolder(me)
    setIssuer(me)
    setIssued(now)
    setNotBefore(now)
    setInfo("info")
    setExtra(extraData)
    license
  }

  final lazy val managementContext: LicenseManagementContext = {
    managementContextBuilder
      .subject("subject")
      .validation(logger debug("Validating license bean: {}", _))
      .build
  }

  def managementContextBuilder: LicenseManagementContextBuilder = applicationContext.context

  final override def repositoryContext: RepositoryContext[Model] = applicationContext.repositoryContext

  final def test1234: PasswordProtection =
    new ObfuscatedPasswordProtection(new ObfuscatedString(Array[Long](0x545a955d0e30826cl, 0x3453ccaa499e6bael))) /* => "test1234" */

  def vendorManager: VendorLicenseManager
}

/** @author Christian Schlichtherle */
object TestContext {

  private val logger = LoggerFactory getLogger classOf[TestContext[_]]
}
