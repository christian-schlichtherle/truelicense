/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.util.Locale.ROOT

import org.scalatest.Matchers._
import org.scalatest._
import org.truelicense.core.codec._

/**
 * @author Christian Schlichtherle
 */
class RepositoryTestSuite
extends WordSpec with ParallelTestExecution { this: TestContext =>

  private val _codec = new SerializationCodec {
    override def contentType = super.contentType.toUpperCase(ROOT)
    override def contentTransferEncoding = super.contentTransferEncoding.toUpperCase(ROOT)
  }

  private def repo = managementContext.repository()

  "A repository" should {
    "sign and verify an object" in {
      val auth = vendorManager.parameters.authentication
      val repo = managementContext.repository()
      val obj = "Hello world!"
      (auth sign (_codec, repo, obj) decode classOf[String]).asInstanceOf[String] should be (obj)
      (auth verify (_codec, repo) decode classOf[String]).asInstanceOf[String] should be (obj)
    }
  }
}
