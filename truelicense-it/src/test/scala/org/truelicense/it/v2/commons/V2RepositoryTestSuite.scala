/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.commons

import org.truelicense.it.core.{RepositoryTestSuite, TestContext}
import org.truelicense.v2.commons.auth.V2RepositoryModel

/** @author Christian Schlichtherle */
abstract class V2RepositoryTestSuite
  extends RepositoryTestSuite[V2RepositoryModel]
{ this: TestContext[V2RepositoryModel] => }
