/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package de.schlichtherle.license;

import global.namespace.truelicense.api.License;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Provides compatibility with V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
// The following is required to support TrueLicense JAX-RS:
// #TRUELICENSE-50: The XML root element name MUST be explicitly defined!
// Otherwise, it would get derived from the class name, but this would break if the class name gets obfuscated, e.g.
// when using the ProGuard configuration from the TrueLicense Maven Archetype.
@XmlRootElement(name = "license-content")
// #TRUELICENSE-52: Dito for the XML type.
// This annotation enables objects of this class to participate in larger object graphs which the application wants to
// encode/decode to/from XML.
@XmlType(name = "license-content")
public class LicenseContent extends License { }
