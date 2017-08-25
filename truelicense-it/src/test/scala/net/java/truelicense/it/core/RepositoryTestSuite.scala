/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.it.core

import java.util.Locale.ROOT

import net.java.truelicense.core.codec._
import org.scalatest.Matchers._
import org.scalatest._

/**
 * @author Christian Schlichtherle
 */
class RepositoryTestSuite
extends WordSpec with ParallelTestExecution { this: TestContext =>

  private val _codec = new SerializationCodec {
    override def contentType: String = super.contentType.toUpperCase(ROOT)
    override def contentTransferEncoding: String = super.contentTransferEncoding.toUpperCase(ROOT)
  }

  private def repo = managementContext.repository()
  private def model = repo.model()

  "A generic repository" when {
    "newly constructed" should {
      "have a null artifact body" in {
        model.getArtifact should be (null)
      }

      "have a null signature" in {
        model.getSignature should be (null)
      }

      "have a null algorithm" in {
        model.getAlgorithm should be (null)
      }
    }

    "signed" should {
      def signed = {
        val authentication = vendorManager.parameters.authentication
        val r = repo
        authentication sign (_codec, r, "Hello world!")
        r.model()
      }

      "have a non-empty artifact" in {
        signed.getArtifact should not be 'isEmpty
      }

      "have a non-empty signature" in {
        signed.getSignature should not be 'isEmpty
      }

      "have a non-empty algorithm" in {
        signed.getAlgorithm should not be 'isEmpty
      }
    }
  }
}
