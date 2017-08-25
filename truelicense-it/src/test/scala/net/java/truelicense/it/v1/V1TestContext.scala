/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.v1

import java.util.Date

import de.schlichtherle.license.LicenseContent
import net.java.truelicense.core._
import net.java.truelicense.core.codec.{Codec, X500PrincipalXmlCodec}
import net.java.truelicense.core.io.Store
import net.java.truelicense.core.policy.PasswordPolicy
import net.java.truelicense.it.core.TestContext
import net.java.truelicense.it.core.TestContext.test1234
import net.java.truelicense.it.v1.V1TestContext._
import org.slf4j.LoggerFactory

/** @author Christian Schlichtherle */
trait V1TestContext extends TestContext {

  final val managementContext: LicenseManagementContext =
    new V1LicenseManagementContext("subject") {
      override def license: LicenseContent = super.license
      override def now: Date = super.now
      override def initialization: LicenseInitialization = {
        val initialization = super.initialization
        new LicenseInitialization {
          def initialize(bean: License) {
            initialization.initialize(bean)
          }
        }
      }
      override def validation: LicenseValidation = {
        val validation = super.validation
        new LicenseValidation {
          def validate(bean: License) {
            validation.validate(bean)
            logger debug ("Validated {}.", bean)
          }
        }
      }
      override def codec: X500PrincipalXmlCodec = super.codec
      override def policy: PasswordPolicy = super.policy
    }

  final def vendorManager: LicenseVendorManager = {
    val vm = vendorContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "private.jks")
        .storePassword(test1234)
        .inject
      .encryption
        .password(test1234)
        .inject
      .build
    require(vm.context eq vendorContext)
    vm
  }

  final def chainedVendorManager: LicenseVendorManager = {
    val vm = vendorContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-private.jks")
        .storePassword(test1234)
        .inject
      .encryption
        .password(test1234)
        .inject
    .build
    require(vm.context eq vendorContext)
    vm
  }

  final def consumerManager(store: Store): LicenseConsumerManager = {
    val cm = consumerContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "public.jks")
        .storePassword(test1234)
        .inject
      .encryption
        .password(test1234)
        .inject
      .storeIn(store)
      .build
    require(cm.context eq consumerContext)
    cm
  }

  final def chainedConsumerManager(parent: LicenseConsumerManager, store: Store): LicenseConsumerManager = {
    val cm = consumerContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "chained-public.jks")
        .storePassword(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .build
    require(cm.context eq consumerContext)
    cm
  }

  final def ftpConsumerManager(parent: LicenseConsumerManager, store: Store): LicenseConsumerManager = {
    val cm = consumerContext.manager
      .keyStore
        .alias("mykey")
        .loadFromResource(prefix + "ftp.jks")
        .storePassword(test1234)
        .inject
      .parent(parent)
      .storeIn(store)
      .ftpDays(1)
      .build
    require(cm.context eq consumerContext)
    cm
  }
}

/** @author Christian Schlichtherle */
object V1TestContext {
  private val logger = LoggerFactory getLogger classOf[V1TestContext]
  private def prefix = classOf[V1TestContext].getPackage.getName.replace('.', '/') + '/'
}
