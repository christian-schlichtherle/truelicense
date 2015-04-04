/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.it.ws.rs

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MediaType._
import javax.ws.rs.ext.ContextResolver

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.sun.jersey.api.client.UniformInterfaceException
import com.sun.jersey.api.core._
import com.sun.jersey.test.framework._
import net.java.truelicense.core._
import net.java.truelicense.it.TestContext
import net.java.truelicense.ws.rs._
import org.junit.Test
import org.scalatest.Matchers._

/** @author Christian Schlichtherle */
class LicenseConsumerServiceITSuite extends JerseyTest { this: TestContext =>

  final lazy val manager = consumerManager
  final lazy val reference = new LicenseBeanAndKeyHolder(vendorManager, license)
  final def licenseClass = reference.bean.getClass

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
    subjectAs(TEXT_PLAIN_TYPE) should be (manager.subject)
    subjectAs(APPLICATION_JSON_TYPE) should be ('"' + manager.subject + '"')
    subjectAs(APPLICATION_XML_TYPE) should
      be ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><subject>" + manager.subject + "</subject>")
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

  final def viewAs(mediaType: MediaType) =
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

  final def verifyAs(mediaType: MediaType) =
    resource path "license" queryParam ("verify", "true") accept mediaType get licenseClass

  def assertFailUninstall() {
    intercept[UniformInterfaceException] { assertUninstall() }
  }

  def assertUninstall() {
    resource path "license" delete ()
  }

  override protected def configure =
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
    override def getContext(ignored: Class[_]) = manager
  }
}
