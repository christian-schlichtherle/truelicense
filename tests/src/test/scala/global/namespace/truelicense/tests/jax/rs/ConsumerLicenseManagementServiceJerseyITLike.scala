/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.tests.jax.rs

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationContext, ObjectMapper, SerializerProvider}
import global.namespace.truelicense.api.{ConsumerLicenseManager, License}
import global.namespace.truelicense.jax.rs.dto.LicenseDTO
import global.namespace.truelicense.jax.rs.{ConsumerLicenseManagementService, ConsumerLicenseManagementServiceExceptionMapper, ObjectMapperResolver}
import global.namespace.truelicense.tests.core.TestContext
import javax.security.auth.x500.X500Principal
import javax.ws.rs.WebApplicationException
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.{Application, MediaType}
import javax.ws.rs.ext.ContextResolver
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test
import org.scalatest.Matchers._

abstract class ConsumerLicenseManagementServiceJerseyITLike
  extends JerseyTest
    with ConsumerLicenseManagementServiceITMixin {
  this: TestContext =>

  override protected def configure: Application = {
    new ResourceConfig(
      classOf[ConsumerLicenseManagementService],
      classOf[ConsumerLicenseManagementServiceExceptionMapper],
      classOf[ObjectMapperResolver]
    ).registerInstances(new AbstractBinder {
      def configure(): Unit = bind(consumerManager()).to(classOf[ConsumerLicenseManager])
    })
  }

  override protected def configureClient(config: ClientConfig): Unit = config.register(MyObjectMapperResolver)

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
      Entity.entity(cachedLicenseKey, APPLICATION_OCTET_STREAM_TYPE),
      classOf[LicenseDTO]
    ).license shouldBe cachedLicenseBean
  }

  private def assertFailView(): Unit = {
    assertFail(assertView())
  }

  private def assertView(): Unit = {
    viewAs(APPLICATION_JSON_TYPE, classOf[LicenseDTO]).license shouldBe cachedLicenseBean
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
    verifyAs(APPLICATION_JSON_TYPE, classOf[LicenseDTO]).license shouldBe cachedLicenseBean
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

  private object MyObjectMapperResolver extends ContextResolver[ObjectMapper] {

    def getContext(aClass: Class[_]): ObjectMapper = objectMapper

    lazy val objectMapper: ObjectMapper = {
      val mapper = new ObjectMapper
      mapper setSerializationInclusion JsonInclude.Include.NON_DEFAULT
      val module = new SimpleModule
      module.addAbstractTypeMapping(classOf[License], licenseClass)
      module.addSerializer(classOf[X500Principal],
        (value: X500Principal, gen: JsonGenerator, _: SerializerProvider) => gen writeString value.getName)
      module.addDeserializer(classOf[X500Principal],
        (p: JsonParser, _: DeserializationContext) => new X500Principal(p readValueAs classOf[String]))
      mapper registerModule module
    }
  }
}
