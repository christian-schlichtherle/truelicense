/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v2.xml

import net.truelicense.it.core.RepositoryCodecTestSuite
import net.truelicense.v2.commons.auth.V2RepositoryModel
import org.junit.runner._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2XmlRepositoryCodecTest
extends RepositoryCodecTestSuite[V2RepositoryModel]
with V2XmlTestContext
