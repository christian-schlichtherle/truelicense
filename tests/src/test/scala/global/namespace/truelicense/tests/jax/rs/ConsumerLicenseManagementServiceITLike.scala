/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.jax.rs

import global.namespace.truelicense.jax.rs.{ConsumerLicenseManagementService, ConsumerLicenseManagementServiceException}
import global.namespace.truelicense.tests.core.TestContext
import org.scalatest.Matchers._
import org.scalatest.WordSpecLike

trait ConsumerLicenseManagementServiceITLike extends WordSpecLike {
  this: TestContext =>

  new State {

    lazy val managementService: ConsumerLicenseManagementService = new ConsumerLicenseManagementService(consumerManager)

    "A consumer license management service" when {
      "using a consumer license manager" when {
        "no license key is installed" should {
          "return its subject" in {
            managementService.subject shouldBe managementContext.subject
          }

          "return its subject encoded as JSON" in {
            managementService.subjectAsJson.subject shouldBe managementContext.subject
          }

          "fail to load the license key as JSON" in {
            intercept[ConsumerLicenseManagementServiceException](managementService loadAsJson false)
          }

          "fail to load and verify the license key as JSON" in {
            intercept[ConsumerLicenseManagementServiceException](managementService loadAsJson true)
          }
        }

        "installing a license key" should {
          "install the license key" in {
            (managementService install licenseKey).getStatus shouldBe 303
          }

          "load the license key as JSON" in {
            (managementService loadAsJson false).license shouldBe licenseBean
          }

          "load and verify the license key as JSON" in {
            (managementService loadAsJson true).license shouldBe licenseBean
          }
        }

        "uninstalling the license key again" should {
          "uninstall the license key" in {
            managementService.uninstall()
          }

          "fail to load the license key as JSON" in {
            intercept[ConsumerLicenseManagementServiceException](managementService loadAsJson false)
          }

          "fail to load and verify the license key as JSON" in {
            intercept[ConsumerLicenseManagementServiceException](managementService loadAsJson true)
          }
        }
      }
    }
  }
}
