/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.jax.rs

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MediaType._
import javax.ws.rs.ext.ContextResolver

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.sun.jersey.api.client.UniformInterfaceException
import com.sun.jersey.api.core._
import com.sun.jersey.test.framework._
import net.java.truelicense.core._
import net.java.truelicense.it.core.TestContext
import net.java.truelicense.ws.rs._
import org.junit.Test
import org.scalatest.Matchers._

/** @author Christian Schlichtherle */
abstract class LicenseConsumerServiceITSuite extends JerseyTest { this: TestContext =>

  final lazy val manager = consumerManager()
  final lazy val reference = new LicenseBeanAndKeyHolder(vendorManager, license)
  final def licenseClass: Class[_ <: License] = reference.bean.getClass

  override protected def configure: LowLevelAppDescriptor =
    new LowLevelAppDescriptor.Builder(resourceConfig).contextPath("").build

  private def resourceConfig: ResourceConfig = {
    val rc = new DefaultResourceConfig
    rc.getClasses.add(classOf[LicenseConsumerService])
    rc.getClasses.add(classOf[LicenseConsumerServiceExceptionMapper])
    rc.getSingletons.add(new LicenseConsumerManagerResolver)
    rc.getSingletons.add(new JacksonJaxbJsonProvider())
    rc
  }

  private final class LicenseConsumerManagerResolver
    extends ContextResolver[LicenseConsumerManager] {
    def getContext(ignored: Class[_]): LicenseConsumerManager = manager
  }

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

  private def assertSubject() {
    subjectAs(TEXT_PLAIN_TYPE) should be (manager.subject)
    subjectAs(APPLICATION_JSON_TYPE) should be ('"' + manager.subject + '"')
    subjectAs(APPLICATION_XML_TYPE) should
      be ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><subject>" + manager.subject + "</subject>")
  }

  private def subjectAs(mediaType: MediaType) =
    resource path "license/subject" accept mediaType get classOf[String]

  private def assertInstall() {
    resource path "license" post reference.key
  }

  private def assertFailView() { assertFail(assertView()) }

  private def assertView() {
    assertView(APPLICATION_JSON_TYPE)
    assertView(APPLICATION_XML_TYPE)
    assertView(TEXT_XML_TYPE)
  }

  private def assertView(mediaType: MediaType) {
    viewAs(mediaType) should equal (reference.bean)
  }

  private final def viewAs(mediaType: MediaType) = {
    resource path "license" accept mediaType get licenseClass
  }

  private def assertFailVerify() { assertFail(assertVerify()) }

  private def assertFail(block: => Any): Unit = {
    val response = intercept[UniformInterfaceException](block).getResponse
    response.getStatus shouldBe 404
    response.getType shouldBe APPLICATION_JSON_TYPE
  }

  private def assertVerify() {
    assertVerify(APPLICATION_JSON_TYPE)
    assertVerify(APPLICATION_XML_TYPE)
    assertVerify(TEXT_XML_TYPE)
  }

  private def assertVerify(mediaType: MediaType) {
    verifyAs(mediaType) should equal (reference.bean)
  }

  private final def verifyAs(mediaType: MediaType) = {
    resource path "license" queryParam("verify", "true") accept mediaType get licenseClass
  }

  private def assertFailUninstall() {
    intercept[UniformInterfaceException] { assertUninstall() }
  }

  private def assertUninstall() {
    resource path "license" delete ()
  }
}
