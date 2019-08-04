/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.jax.rs

import global.namespace.truelicense.api.ConsumerLicenseManager
import global.namespace.truelicense.jax.rs.dto.LicenseDTO
import global.namespace.truelicense.jax.rs.{ConsumerLicenseManagementService, ConsumerLicenseManagementServiceExceptionMapper, ObjectMapperResolver}
import global.namespace.truelicense.tests.core.TestContext
import javax.ws.rs.WebApplicationException
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.{Application, MediaType}
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test
import org.scalatest.Matchers._

abstract class ConsumerLicenseManagementServiceJerseyITLike extends JerseyTest {
  this: TestContext =>

  private lazy val objectMapperResolver = new ObjectMapperResolver(managementContext.licenseFactory.licenseClass)

  private lazy val state = new State
  import state._

  override protected def configure: Application = {
    new ResourceConfig(
      classOf[ConsumerLicenseManagementService],
      classOf[ConsumerLicenseManagementServiceExceptionMapper],
      classOf[JacksonFeature]
    ).registerInstances(
      objectMapperResolver,
      new AbstractBinder {

        def configure(): Unit = bind(state.consumerManager).to(classOf[ConsumerLicenseManager])
      }
    )
  }

  override protected def configureClient(config: ClientConfig): Unit = {
    config
      .register(classOf[JacksonFeature])
      .register(objectMapperResolver)
  }

  @Test
  def testLifeCycle(): Unit = {
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

  private def assertSubject(): Unit = {
    val subject = managementContext.subject
    subjectAs(TEXT_PLAIN_TYPE) shouldBe subject
    subjectAs(APPLICATION_JSON_TYPE) shouldBe s"""{"subject":"$subject"}"""
  }

  private def subjectAs(mediaType: MediaType) = target("license/subject") request mediaType get classOf[String]

  private def assertInstall(): Unit = {
    target("license").request.post(
      Entity.entity(licenseKey, APPLICATION_OCTET_STREAM_TYPE),
      classOf[LicenseDTO]
    ).license shouldBe licenseBean
  }

  private def assertFailView(): Unit = {
    assertFail(assertView())
  }

  private def assertView(): Unit = {
    viewAs(APPLICATION_JSON_TYPE, classOf[LicenseDTO]).license shouldBe licenseBean
  }

  private final def viewAs[T](mediaType: MediaType, responseType: Class[T]): T = {
    target("license") request mediaType get responseType
  }

  private def assertFailVerify(): Unit = {
    assertFail(assertVerify())
  }

  private def assertFail(what: => Any): Unit = {
    val response = intercept[WebApplicationException](what).getResponse
    response.getStatusInfo shouldBe NOT_FOUND
    response.getMediaType shouldBe APPLICATION_JSON_TYPE
  }

  private def assertVerify(): Unit = {
    verifyAs(APPLICATION_JSON_TYPE, classOf[LicenseDTO]).license shouldBe licenseBean
  }

  private final def verifyAs[T](mediaType: MediaType, responseType: Class[T]): T = {
    target("license") queryParam("verify", "true") request mediaType get responseType
  }

  private def assertUninstallSuccess(): Unit = {
    uninstallStatus() shouldBe 204
  }

  private def assertUninstallFailure(): Unit = {
    uninstallStatus() shouldBe 404
  }

  private def uninstallStatus(): Int = target("license").request.delete().getStatus
}
