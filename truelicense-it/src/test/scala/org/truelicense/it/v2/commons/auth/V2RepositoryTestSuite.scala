/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.json

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.truelicense.it.core.{RepositoryTestSuite, TestContext}
import org.truelicense.v2.commons.auth.{V2RepositoryContext, V2RepositoryModel}

/** @author Christian Schlichtherle */
abstract class V2RepositoryTestSuite
  extends RepositoryTestSuite[V2RepositoryModel] { this: TestContext =>

  override def repositoryContext = new V2RepositoryContext
}
