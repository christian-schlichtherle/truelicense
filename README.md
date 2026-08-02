[![Release Notes](https://img.shields.io/github/v/release/christian-schlichtherle/truelicense)](https://github.com/christian-schlichtherle/truelicense/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/global.namespace.truelicense/truelicense)](https://search.maven.org/search?q=g:global.namespace.truelicense) 
[![License](https://img.shields.io/github/license/christian-schlichtherle/truelicense)](https://github.com/christian-schlichtherle/truelicense/blob/master/LICENSE)
[![Test Workflow](https://github.com/christian-schlichtherle/truelicense/workflows/test/badge.svg)](https://github.com/christian-schlichtherle/truelicense/actions?query=workflow%3Atest)

# TrueLicense

## User Documentation

https://truelicense.namespace.global

## Building

Use the Maven Wrapper — it pins the required Maven version, so nothing needs installing beyond a JDK:

```bash
./mvnw install
```

The build runs on every LTS release from JDK 8 through 25; CI tests all of them. The artifacts always target Java 8
bytecode regardless of the JDK used.

## Releasing

Tag and push:

```bash
git tag v4.1.0
git push origin v4.1.0
```

`.github/workflows/release.yml` takes it from there: the tag sets the version, and the build runs the tests, signs
the artifacts and publishes them to Maven Central. Nothing is committed back, so the version in `pom.xml` stays a
snapshot.

The workflow needs four repository secrets — `MAVEN_GPG_PRIVATE_KEY`, `MAVEN_GPG_PASSPHRASE`,
`CENTRAL_TOKEN_USERNAME` and `CENTRAL_TOKEN_PASSWORD`; the token pair comes from
[central.sonatype.com](https://central.sonatype.com) under *View Account → Generate User Token*.
