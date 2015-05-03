/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v1

import de.schlichtherle.xml.GenericCertificate
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.truelicense.it.core.RepositoryTestSuite
import org.truelicense.v1.auth.V1RepositoryContext

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V1RepositoryTest
extends RepositoryTestSuite[GenericCertificate]
with V1TestContext
