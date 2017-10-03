/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.v1

import de.schlichtherle.xml.GenericCertificate
import net.truelicense.it.core.RepositoryCodecTestSuite
import org.junit.runner._
import org.scalatest.junit._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V1RepositoryCodecTest
extends RepositoryCodecTestSuite[GenericCertificate]
with V1TestContext
