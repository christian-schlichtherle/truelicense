/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core

import java.util.Calendar._
import java.util.Date
import javax.security.auth.x500.X500Principal

import net.truelicense.api.auth.{RepositoryContext, RepositoryContextProvider}
import net.truelicense.api.codec.{Codec, CodecProvider}
import net.truelicense.api.io.{Store, Transformation}
import net.truelicense.api.passwd.PasswordProtection
import net.truelicense.api.{ConsumerLicenseManager, License, VendorLicenseManager}
import net.truelicense.core.TrueLicenseApplicationContext
import net.truelicense.it.core.TestContext._
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
trait TestContext[Model <: AnyRef]
  extends CodecProvider
  with RepositoryContextProvider[Model] {

  val applicationContext: TrueLicenseApplicationContext[Model]

  def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager

  def chainedVendorManager: VendorLicenseManager

  final override def codec: Codec = managementContext.codec

  final def compression: Transformation = applicationContext.compression

  final def consumerManager(): ConsumerLicenseManager = consumerManager(store)

  def consumerManager(store: Store): ConsumerLicenseManager

  final def datePlusDays(date: Date, days: Int): Date = {
    val cal = getInstance
    import cal._
    setTime(date)
    assert(isLenient)
    add(DATE, days)
    getTime
  }

  def encryption: Transformation

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

  final lazy val managementContext = {
    applicationContext
      .context
      .subject("subject")
      .validation(logger debug("Validating license bean: {}", _))
      .build
  }

  final override def repositoryContext: RepositoryContext[Model] = applicationContext.repositoryContext

  def store: Store = managementContext.memoryStore

  final def test1234: PasswordProtection = applicationContext protection
    Array[Long](0x545a955d0e30826cl, 0x3453ccaa499e6bael) /* => "test1234" */

  final def transformation: Transformation = compression andThen encryption

  def vendorManager: VendorLicenseManager
}

/** @author Christian Schlichtherle */
object TestContext {

  private val logger = LoggerFactory getLogger classOf[TestContext[_]]
}
