[![Release Notes](https://img.shields.io/github/v/release/christian-schlichtherle/truelicense)](https://github.com/christian-schlichtherle/truelicense/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/global.namespace.truelicense/truelicense)](https://central.sonatype.com/artifact/global.namespace.truelicense/truelicense)
[![Apache License 2.0](https://img.shields.io/github/license/christian-schlichtherle/truelicense)](https://www.apache.org/licenses/LICENSE-2.0)
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
git tag v4.2.0
git push origin v4.2.0
```

`.github/workflows/release.yml` takes it from there: the tag sets the version, the full JDK 8-25 test matrix runs as
a gate, and the build then signs the artifacts, publishes them to Maven Central and creates the GitHub release from
the tag. Nothing is committed back, so the version in `pom.xml` stays a snapshot.

The workflow needs four repository secrets — `MAVEN_GPG_PRIVATE_KEY`, `MAVEN_GPG_PASSPHRASE`,
`CENTRAL_TOKEN_USERNAME` and `CENTRAL_TOKEN_PASSWORD`; the token pair comes from
[central.sonatype.com](https://central.sonatype.com) under *View Account → Generate User Token*.
