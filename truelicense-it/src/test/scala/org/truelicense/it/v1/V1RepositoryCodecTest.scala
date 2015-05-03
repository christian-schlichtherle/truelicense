/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1

import de.schlichtherle.xml.GenericCertificate
import org.junit.runner._
import org.scalatest.junit._
import org.truelicense.it.core.RepositoryCodecTestSuite

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V1RepositoryCodecTest
extends RepositoryCodecTestSuite[GenericCertificate]
with V1TestContext
