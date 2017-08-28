/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.jax.rs

import net.truelicense.it.core.TestContext
import net.truelicense.jax.rs.{ConsumerLicenseManagementService, ConsumerLicenseManagementServiceException}
import org.scalatest.Matchers._
import org.scalatest.WordSpec

/** @author Christian Schlichtherle */
abstract class ConsumerLicenseManagementServiceTestSuite
extends WordSpec { this: TestContext[_] =>

  "A consumer license management service" when {
    "using a consumer license manager" when {
      // These must be `lazy vals` or otherwise you will get a
      // `NullPointerException` from the `store` method of the mixed in
      // `TestContext` class.
      lazy val manager = consumerManager()
      lazy val reference = new LicenseBeanAndKeyHolder(vendorManager, license)
      lazy val service = new ConsumerLicenseManagementService(manager)

      "no license key is installed" should {
        "return its subject" in {
          service.subject should be (managementContext.subject)
        }

        "return its subject encoded as JSON" in {
          service.subjectAsJson should be ('"' + managementContext.subject + '"')
        }

        "return its subject encoded as XML" in {
          service.subjectAsXml.getValue should be (managementContext.subject)
        }

        "fail to load the license key in" in {
          intercept[ConsumerLicenseManagementServiceException] (service view false)
        }

        "fail to verify and load the license key in" in {
          intercept[ConsumerLicenseManagementServiceException] (service view true)
        }
      }

      "installing a license key" should {
        "install the license key" in {
          service install reference.key
        }

        "load the license key in" in {
          (service view false) should equal (reference.bean)
        }

        "verify and load the license key in" in {
          (service view true) should equal (reference.bean)
        }
      }

      "uninstalling the license key again" should {
        "uninstall the license key" in {
          service uninstall ()
        }

        "fail to load the license key in" in {
          intercept[ConsumerLicenseManagementServiceException] (service view false)
        }

        "fail to verify and load the license key in" in {
          intercept[ConsumerLicenseManagementServiceException] (service view true)
        }
      }
    }
  }
}
