/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.jax.rs

import net.truelicense.it.core.TestContext
import net.truelicense.jax.rs.ConsumerLicenseManagementServiceException
import org.scalatest.Matchers._
import org.scalatest.WordSpec

/** @author Christian Schlichtherle */
abstract class ConsumerLicenseManagementServiceTestSuite
  extends WordSpec
    with ConsumerLicenseManagementServiceTestMixin {
  this: TestContext[_] =>

  "A consumer license management service" when {
    "using a consumer license manager" when {
      "no license key is installed" should {
        "return its subject" in {
          managementService.subject shouldBe managementContext.subject
        }

        "return its subject encoded as JSON" in {
          managementService.subjectAsJson.subject shouldBe managementContext.subject
        }

        "return its subject encoded as XML" in {
          managementService.subjectAsXml.getValue shouldBe managementContext.subject
        }

        "fail to load the license key in" in {
          intercept[ConsumerLicenseManagementServiceException](managementService view false)
        }

        "fail to verify and load the license key in" in {
          intercept[ConsumerLicenseManagementServiceException](managementService view true)
        }
      }

      "installing a license key" should {
        "install the license key" in {
          (managementService install cachedLicenseKey).getStatus shouldBe 303
        }

        "load the license key in" in {
          managementService view false shouldBe cachedLicenseBean
        }

        "verify and load the license key in" in {
          managementService view true shouldBe cachedLicenseBean
        }
      }

      "uninstalling the license key again" should {
        "uninstall the license key" in {
          managementService uninstall ()
        }

        "fail to load the license key in" in {
          intercept[ConsumerLicenseManagementServiceException](managementService view false)
        }

        "fail to verify and load the license key in" in {
          intercept[ConsumerLicenseManagementServiceException](managementService view true)
        }
      }
    }
  }
}
