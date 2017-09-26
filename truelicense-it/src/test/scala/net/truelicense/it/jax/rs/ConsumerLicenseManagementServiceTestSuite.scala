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

        "fail to view the license key as XML" in {
          intercept[ConsumerLicenseManagementServiceException](managementService viewAsXml false)
        }

        "fail to verify and view the license key as XML" in {
          intercept[ConsumerLicenseManagementServiceException](managementService viewAsXml true)
        }

        "fail to view the license key as JSON" in {
          intercept[ConsumerLicenseManagementServiceException](managementService viewAsJson false)
        }

        "fail to verify and view the license key as JSON" in {
          intercept[ConsumerLicenseManagementServiceException](managementService viewAsJson true)
        }
      }

      "installing a license key" should {
        "install the license key" in {
          (managementService install cachedLicenseKey).getStatus shouldBe 303
        }

        "view the license key as XML" in {
          managementService viewAsXml false shouldBe cachedLicenseBean
        }

        "verify and view the license key as XML" in {
          managementService viewAsXml true shouldBe cachedLicenseBean
        }

        "view the license key as JSON" in {
          (managementService viewAsJson false).license shouldBe cachedLicenseBean
        }

        "verify and view the license key as JSON" in {
          (managementService viewAsJson true).license shouldBe cachedLicenseBean
        }
      }

      "uninstalling the license key again" should {
        "uninstall the license key" in {
          managementService uninstall ()
        }

        "fail to view the license key as XML" in {
          intercept[ConsumerLicenseManagementServiceException](managementService viewAsXml false)
        }

        "fail to verify and view the license key as XML" in {
          intercept[ConsumerLicenseManagementServiceException](managementService viewAsXml true)
        }

        "fail to view the license key as JSON" in {
          intercept[ConsumerLicenseManagementServiceException](managementService viewAsJson false)
        }

        "fail to verify and view the license key as JSON" in {
          intercept[ConsumerLicenseManagementServiceException](managementService viewAsJson true)
        }
      }
    }
  }
}
