---
name: release
description: How TrueLicense is published to Maven Central, the constraints of the central-publishing-maven-plugin wiring, and how to rehearse a release locally. Use when cutting a release, changing anything in the publishing setup or the sonatype-oss-release profile, or debugging what does or does not reach Central.
---

# Releasing

Pushing a `v*` tag triggers `.github/workflows/release.yml`, which derives the version from the tag, runs the full
build on JDK 8 and publishes to the Central Publisher Portal. Nothing is committed back; `pom.xml` stays a snapshot.
See README.md for the secrets it needs.

Constraints worth knowing before changing any of this:

- `central-publishing-maven-plugin` declares `<extensions>true</extensions>`, so its lifecycle participant loads on
  **every** build that activates `sonatype-oss-release` — including the JDK 8 compile job, not just `deploy`. Any
  replacement has to stay Java 8 compatible.
- That participant removes `maven-deploy-plugin` and injects a `publish` goal into every module's `deploy` phase.
  `maven.deploy.skip` is therefore inert, and there is no per-module opt-out: it declines to install itself only if
  some module declares the plugin with its own executions, and that decision is global. `skipPublishing` is
  evaluated after staging, so it does not spare a module whose artifact has no file.
- Hence `tests` builds an empty jar, and `excludeArtifacts` keeps it out of the bundle. Both are needed:
  the first so the release does not abort on the last module, the second so an integration-test module never
  reaches Central.
- `sonatype-oss-release` is what enables javadoc, sources, signing and `verify-obfuscate-main-classes`. Only the two
  workflows activate it. Anything that publishes without it publishes unobfuscated artifacts — which is how 4.0.1
  and 4.0.3 shipped.

## Dry run

The release path has no CI coverage until a tag is pushed, so rehearse it locally before a first release or after
changing the wiring. An unreachable endpoint and a throwaway settings file make publishing impossible:

```bash
./mvnw versions:set -DnewVersion=4.1.0-PORTALTEST          # the bundle path only runs for a non-SNAPSHOT version
./mvnw -s /tmp/dryrun-settings.xml clean deploy -P sonatype-oss-release \
       -DskipTests -DcentralBaseUrl=http://127.0.0.1:1
unzip -l target/central-publishing/central-bundle.zip
./mvnw versions:revert
```

where `/tmp/dryrun-settings.xml` holds a `<server><id>central</id>` with deliberately invalid credentials. Failing at
the last module with `Deployment failed while publishing` is the expected outcome — that is the upload hitting the
dead endpoint. Check the bundle rather than the log: `.jar`, `-sources.jar`, `-javadoc.jar`, `.pom` and a `.asc` for
each, across all 15 published modules plus the root POM, and no `truelicense-tests`. Signing needs the passphrase
cached in the gpg-agent, or `pinentry` blocks the build; add `-Dgpg.keyname=<fingerprint>` if your keyring holds
more than one usable key.

Nothing local covers the Portal's own validation of the bundle.

## After a release

Bump the `truelicense-maven-plugin` pin in the root POM's `<pluginManagement>` to the version just published — the
project builds itself with its own previously released plugin — and re-run the JDK 8 / 17 / 25 builds. See
*Bootstrapping gotcha* in `CLAUDE.md`.
