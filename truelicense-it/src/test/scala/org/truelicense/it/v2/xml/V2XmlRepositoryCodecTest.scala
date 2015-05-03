/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.xml

import org.truelicense.it.core.RepositoryCodecTestSuite
import org.junit.runner._
import org.scalatest.junit._
import org.truelicense.v2.commons.auth.V2RepositoryModel

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2XmlRepositoryCodecTest
extends RepositoryCodecTestSuite[V2RepositoryModel]
with V2XmlTestContext
