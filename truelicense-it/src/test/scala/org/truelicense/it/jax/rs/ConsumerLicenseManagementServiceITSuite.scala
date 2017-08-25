/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.jax.rs

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MediaType._
import javax.ws.rs.ext.ContextResolver

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.sun.jersey.api.client.UniformInterfaceException
import com.sun.jersey.api.core._
import com.sun.jersey.test.framework._
import org.junit.Test
import org.scalatest.Matchers._
import org.truelicense.api.ConsumerLicenseManager
import org.truelicense.it.core.TestContext
import org.truelicense.jax.rs._

/** @author Christian Schlichtherle */
abstract class ConsumerLicenseManagementServiceITSuite extends JerseyTest {

  self: TestContext[_] =>

  final lazy val reference = new LicenseBeanAndKeyHolder(vendorManager, license)
  final def licenseClass: Class[_ <: License] = reference.bean.getClass

  @Test def testLifeCycle() {
    assertSubject()
    assertFailView()
    assertFailVerify()
    assertFailUninstall()
    assertInstall()
    assertSubject()
    assertView()
    assertVerify()
    assertUninstall()
    assertSubject()
    assertFailView()
    assertFailVerify()
    assertFailUninstall()
  }

  def assertSubject() {
    val subject = managementContext.subject
    subjectAs(TEXT_PLAIN_TYPE) should be (subject)
    subjectAs(APPLICATION_JSON_TYPE) should be ('"' + subject + '"')
    subjectAs(APPLICATION_XML_TYPE) should
      be ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><subject>" + subject + "</subject>")
  }

  private def subjectAs(mediaType: MediaType) =
    resource path "license/subject" accept mediaType get classOf[String]

  def assertInstall() {
    resource path "license" post reference.key
  }

  def assertFailView() {
    intercept[UniformInterfaceException] { assertView() }
  }

  def assertView() {
    assertView(APPLICATION_JSON_TYPE)
    assertView(APPLICATION_XML_TYPE)
    assertView(TEXT_XML_TYPE)
  }

  def assertView(mediaType: MediaType) {
    viewAs(mediaType) should equal (reference.bean)
  }

  final def viewAs(mediaType: MediaType): License =
    resource path "license" accept mediaType get licenseClass

  def assertFailVerify() {
    intercept[UniformInterfaceException] { assertVerify() }
  }

  def assertVerify() {
    assertVerify(APPLICATION_JSON_TYPE)
    assertVerify(APPLICATION_XML_TYPE)
    assertVerify(TEXT_XML_TYPE)
  }

  def assertVerify(mediaType: MediaType) {
    verifyAs(mediaType) should equal (reference.bean)
  }

  final def verifyAs(mediaType: MediaType): License =
    resource path "license" queryParam ("verify", "true") accept mediaType get licenseClass

  def assertFailUninstall() {
    intercept[UniformInterfaceException] { assertUninstall() }
  }

  def assertUninstall() {
    resource path "license" delete ()
  }

  override protected def configure: LowLevelAppDescriptor =
    new LowLevelAppDescriptor.Builder(resourceConfig).contextPath("").build

  private def resourceConfig: ResourceConfig = {
    val rc = new DefaultResourceConfig
    rc.getClasses.add(classOf[ConsumerLicenseManagementService])
    rc.getClasses.add(classOf[ConsumerLicenseManagementServiceExceptionMapper])
    rc.getSingletons.add(new ConsumerLicenseManagerResolver)
    rc.getSingletons.add(new JacksonJaxbJsonProvider())
    rc
  }

  private final class ConsumerLicenseManagerResolver
  extends ContextResolver[ConsumerLicenseManager] {

    lazy val manager = consumerManager()

    def getContext(ignored: Class[_]) = manager
  }
}
