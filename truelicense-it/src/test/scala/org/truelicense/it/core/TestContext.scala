/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.util.Calendar._
import java.util.Date
import javax.security.auth.x500.X500Principal

import org.slf4j.LoggerFactory
import org.truelicense.api._
import org.truelicense.api.auth.RepositoryContextProvider
import org.truelicense.api.codec.CodecProvider
import org.truelicense.api.io.{Store, Transformation}
import org.truelicense.core.TrueLicenseApplicationContext
import org.truelicense.it.core.TestContext._
import org.truelicense.spi.io.Transformer

/** @author Christian Schlichtherle */
trait TestContext[Model <: AnyRef]
  extends CodecProvider
  with RepositoryContextProvider[Model] {

  val applicationContext: TrueLicenseApplicationContext[Model]

  def chainedConsumerManager(parent: ConsumerLicenseManager, store: Store): ConsumerLicenseManager

  def chainedVendorManager: VendorLicenseManager

  final override def codec = managementContext.codec

  final def compression = applicationContext.compression

  final def consumerManager(): ConsumerLicenseManager = consumerManager(store)

  def consumerManager(store: Store): ConsumerLicenseManager

  final def datePlusDays(date: Date, days: Int) = {
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

  final def license = {
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

  final lazy val managementContext =
    applicationContext.context
      .subject("subject")
      .validation(new LicenseValidation {
          override def validate(bean: License) {
            logger debug ("Validating license bean: {}", bean)
          }
        })
      .build

  final override def repositoryContext = applicationContext.repositoryContext

  def store = managementContext.memoryStore

  final def test1234 = applicationContext protection
    Array[Long](0x545a955d0e30826cl, 0x3453ccaa499e6bael) /* => "test1234" */

  final def transformation = Transformer apply compression `then` encryption get ()

  def vendorManager: VendorLicenseManager
}

/** @author Christian Schlichtherle */
object TestContext {

  private val logger = LoggerFactory getLogger classOf[TestContext[_]]
}
