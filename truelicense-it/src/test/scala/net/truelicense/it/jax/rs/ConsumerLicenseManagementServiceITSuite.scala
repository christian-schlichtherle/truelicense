/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.jax.rs

import javax.ws.rs.WebApplicationException
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType._
import javax.ws.rs.core.{Application, MediaType}

import net.truelicense.api.License
import net.truelicense.it.core.TestContext
import net.truelicense.jax.rs.ConsumerLicenseManagementServiceExceptionMapper
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test
import org.scalatest.Matchers._

/** @author Christian Schlichtherle */
abstract class ConsumerLicenseManagementServiceITSuite
  extends JerseyTest
    with ConsumerLicenseManagementServiceTestMixin {
  self: TestContext[_] =>

  override protected def configure: Application = {
    new ResourceConfig(classOf[ConsumerLicenseManagementServiceExceptionMapper]).registerInstances(managementService)
  }

  @Test
  def testLifeCycle() {
    assertSubject()
    assertFailView()
    assertFailVerify()
    assertUninstallFailure()
    assertInstall()
    assertSubject()
    assertView()
    assertVerify()
    assertUninstallSuccess()
    assertSubject()
    assertFailView()
    assertFailVerify()
    assertUninstallFailure()
  }

  def assertSubject() {
    val subject = managementContext.subject
    subjectAs(TEXT_PLAIN_TYPE) shouldBe subject
    subjectAs(APPLICATION_JSON_TYPE) shouldBe s""""$subject""""
    subjectAs(APPLICATION_XML_TYPE) shouldBe s"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?><subject>$subject</subject>"""
  }

  private def subjectAs(mediaType: MediaType) = target("license/subject") request mediaType get classOf[String]

  def assertInstall() {
    target("license").request.post(
      Entity.entity(cachedLicenseKey, MediaType.APPLICATION_OCTET_STREAM_TYPE),
      cachedLicenseClass
    ) shouldBe cachedLicenseBean
  }

  def assertFailView() {
    intercept[WebApplicationException] {
      assertView()
    }
  }

  def assertView() {
    assertView(APPLICATION_JSON_TYPE)
    assertView(APPLICATION_XML_TYPE)
    assertView(TEXT_XML_TYPE)
  }

  def assertView(mediaType: MediaType) {
    viewAs(mediaType) shouldBe cachedLicenseBean
  }

  final def viewAs(mediaType: MediaType): License = target("license") request mediaType get cachedLicenseClass

  def assertFailVerify() {
    intercept[WebApplicationException] {
      assertVerify()
    }
  }

  def assertVerify() {
    assertVerify(APPLICATION_JSON_TYPE)
    assertVerify(APPLICATION_XML_TYPE)
    assertVerify(TEXT_XML_TYPE)
  }

  def assertVerify(mediaType: MediaType) {
    verifyAs(mediaType) shouldBe cachedLicenseBean
  }

  final def verifyAs(mediaType: MediaType): License = {
    target("license") queryParam ("verify", "true") request mediaType get cachedLicenseClass
  }

  def assertUninstallSuccess() {
    uninstallStatus() shouldBe 204
  }

  def assertUninstallFailure() {
    uninstallStatus() shouldBe 404
  }

  private def uninstallStatus() = target("license").request.delete().getStatus
}
