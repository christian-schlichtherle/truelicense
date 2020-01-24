[![Release Notes](https://img.shields.io/github/release/christian-schlichtherle/truelicense.svg)](https://github.com/christian-schlichtherle/truelicense/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/global.namespace.truelicense/truelicense.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.truelicense%22) 
[![Apache License 2.0](https://img.shields.io/github/license/christian-schlichtherle/truelicense.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://api.travis-ci.org/christian-schlichtherle/truelicense.svg)](https://travis-ci.org/christian-schlichtherle/truelicense)

# TrueLicense 

TrueLicense is an open source engine for license management on the Java Virtual Machine.
Due to its functional and modular design, it scales from simple to complex licensing schemas.
TrueLicense features various interfaces for managing free trial periods, subscriptions, multiple editions,
internationalization, privacy protection and more.

Before version 4, TrueLicense was covered by the GNU Affero General Public License, Version 3.
Since version 4, it is covered by the Apache License, Version 2.0.

The old documentation at https://truelicense.net/ is now obsolete and will be updated eventually.
In the mean time, please still use it as your reference.
For a quick start, here's how you can generate a sample project using the [TrueLicense Maven Archetype](https://github.com/christian-schlichtherle/truelicense-maven-archetype) with the new V4 license key format:

```bash
$ mvn org.apache.maven.plugins:maven-archetype-plugin:3.1.0:generate \
    -DarchetypeGroupId=global.namespace.truelicense-maven-archetype \
    -DarchetypeArtifactId=truelicense-maven-archetype \
    -DarchetypeVersion=4.0.2 \
    -DartifactId=basic \
    -Dcompany='Company Inc.' \
    -DgroupId=com.company.product \
    -Dpassword=test1234 \
    -Dsubject='StarGazer 2020' \
    -Dversion=1.0-SNAPSHOT
$ cd basic
$ chmod +x mvnw
$ ./mvnw clean verify
```

Next, you can generate and install a license key like this:

```bash
$ java -jar keygen/target/*-keygen-*-standalone.jar generate license.lic -output -
{"consumerAmount":1,"consumerType":"User","holder":"CN=Unknown","issued":1565085418292,"issuer":"CN=Company Inc.","subject":"StarGazer 2020"}
$ java -jar keymgr/target/*-keymgr-*-guarded.jar wizard
```

Follow the instructions of the licensing wizard to install, view and uninstall the license key previously saved to the
`license.lic` file.
