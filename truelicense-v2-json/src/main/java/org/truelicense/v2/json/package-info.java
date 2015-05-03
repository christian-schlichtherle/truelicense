/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

/**
 * Provides life cycle management for Version-2-with-JSON (V2/JSON) format
 * license keys.
 * License keys are small binary objects which can be stored in a file, a
 * preferences node, the heap or any custom implementation of the
 * {@link org.truelicense.api.io.Store} interface.
 * License keys pass the following life cycle phases:
 * <ol>
 * <li>{@linkplain org.truelicense.api.LicenseVendorManager#generator Generation},
 * <li>{@linkplain org.truelicense.api.LicenseConsumerManager#install installation},
 * <li>{@linkplain org.truelicense.api.LicenseConsumerManager#view viewing},
 * <li>{@linkplain org.truelicense.api.LicenseConsumerManager#verify verification} and
 * <li>{@linkplain org.truelicense.api.LicenseConsumerManager#uninstall uninstallation}
 * </ol>
 * <p>
 * ... as defined by the
 * {@link org.truelicense.api.LicenseVendorManager} and the
 * {@link org.truelicense.api.LicenseConsumerManager} interfaces.
 * A Free Trial Period (FTP) is
 * {@linkplain org.truelicense.api.LicenseConsumerContext#ftpManager configurable}
 * with a license consumer manager in order to ease the conversion of prospects
 * to customers.
 * License consumer managers can also get
 * {@linkplain org.truelicense.api.LicenseConsumerContext#chainedManager chained}
 * in order to unlock application features based on the purchased license keys.
 * <p>
 * A license is an instance of the {@link org.truelicense.api.License}
 * class.
 * This class follows the Java Bean pattern and defines common properties for
 * the licensing subject, issue date, issuer, holder, consumer type/amount,
 * validity period and custom data.
 * These properties get
 * {@linkplain org.truelicense.api.LicenseInitialization#initialize initialized}
 * and
 * {@linkplain org.truelicense.api.LicenseValidation#validate validated}
 * whenever a license key gets generated.
 * They also get validated whenever a license key gets installed or verified.
 * <p>
 * A license key gets generated from an instance of the {@code License} class by
 * applying the following function composition:
 * <pre>
 * {@code encrypt(compress(encode(sign(encode(validate(initialize(duplicate(license))))))))}
 * </pre>
 * <p>
 * When installing or verifying a license key, a duplicate of the original
 * {@code License} object gets derived by applying the following (almost
 * inverse) function composition (note that there is no initialization and
 * hence no duplication required):
 * <pre>
 * {@code validate(decode(authenticate(decode(decompress(decrypt(licenseKey))))))}
 * </pre>
 * <p>
 * The validation step is skipped when a license key is just viewed.
 * Encryption is done via JCA with a configurable password based encryption
 * algorithm.
 * Authentication is done via JCA with a configurable key store type and
 * signature algorithm.
 * Encoding is done with {@link java.beans.XMLEncoder} for simple,
 * schema-less, yet resilient long term archival.
 * <p>
 * Applications use the
 * {@link org.truelicense.v2.json.V2JsonLicenseManagementContext} class as
 * their starting point for configuring a license vendor or consumer manager.
 * This class can get subclassed to customize special aspects, such as
 * performing additional license validation steps, providing an authoritative
 * clock et al.
 * However, TrueLicense 2 has been designed so that typical applications don't
 * need to subclass or implement any class or interface of its API.
 * <p>
 * For security, passwords are represented as an instance of the
 * {@link org.truelicense.obfuscate.ObfuscatedString} class.
 * Other security critical string (algorithms, paths, etc.) are annotatable
 * with {@literal @}{@link org.truelicense.obfuscate.Obfuscate} and
 * processible with the TrueLicense Maven Plugin for simple, reliable
 * obfuscation of constant string values in the byte code for license
 * applications.
 * <p>
 * <strong>Warning:</strong>
 * To protect against reverse engineering and modification of your
 * application, it is strictly necessary to generally process <em>all</em> byte
 * code which is comprised in your deployment unit with a tool for byte code
 * obfuscation.
 * TrueLicense does not provide general byte code obfuscation because there
 * exist good quality third party tools, e.g. ProGuard.
 *
 * @author Christian Schlichtherle
 */
package org.truelicense.v2.json;
