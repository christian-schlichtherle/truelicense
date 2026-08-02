# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

TrueLicense is a license management engine for the JVM (Apache 2.0). User documentation lives at
https://truelicense.namespace.global — this repo contains only the engine, not the docs site.

Multi-module Maven build, group `global.namespace.truelicense`, inheriting from `global.namespace.parent-pom:16`.
Main sources are Java 8; tests are Scala 2.13 / ScalaTest 3.2.

**Maven 3.9.16 or newer is required** and enforced — the build fails at `validate` on anything older. This is not
cosmetic: Maven 3.6.x silently breaks constant string obfuscation (see below). **Use `./mvnw`**, which pins exactly
3.9.16, so the requirement never has to be satisfied by hand. It is the script-only wrapper (no jar in the repo);
`.mvn/wrapper/maven-wrapper.properties` holds the pinned version and CI uses it too.

## Build & test

```bash
./mvnw install                 # full build; do this first — modules depend on each other
./mvnw verify                  # what CI runs
./mvnw -pl <module> test       # test one module (add -am to also build its dependencies)
./mvnw -q -o ...               # -o (offline) is fine once ~/.m2 is populated
```

Tests are run by **scalatest-maven-plugin**, not Surefire: the `scala-test-sources` profile in the parent POM
auto-activates on `src/test/scala` and disables Surefire's `default-test`. Consequences:

- Run a single suite with `-Dsuites`, *not* `-Dtest`:
  `./mvnw -pl tests test -Dsuites='global.namespace.truelicense.tests.v4.V4LicenseKeyLifeCycleIT'`
- Append `@ some test name` to `-Dsuites` to run a single test within a suite.
- Most `*IT` suites are ScalaTest suites executed by scalatest-maven-plugin in the **`test`** phase, so `./mvnw test`
  runs them.
- **But `./mvnw test` is not the whole test suite.** The parent POM's `java-test-sources` profile also binds Failsafe
  at `integration-test` in the modules that have a `src/test/java` directory (`api` and `tests`). In `tests` it runs
  the four `*ConsumerLicenseManagementServiceJerseyIT` suites, which are JUnit/JerseyTest classes that ScalaTest
  does not pick up. Use `./mvnw verify` (or `install`) to run everything; each format has a ScalaTest `*Spec` and a
  Failsafe `*JerseyIT` counterpart on purpose.

### Toolchain constraints

- Compile with JDK 8 (`maven.compiler.source/target` is 1.8 from the parent POM).
- Do **not** run tests on JDK 15+: scalatest-maven-plugin fails to load test classes there. CI compiles on JDK 8
  and then runs `./mvnw verify` on JDK 14.
- On macOS, select JDK 8 with `JAVA_HOME=$(/usr/libexec/java_home -v 1.8)`. **`-v 8` does not work** — a bare major
  number means "8 *or newer*", so it returns the newest installed JDK, and the build then silently runs on that.
  Always confirm the `Java version:` line that `--show-version` prints.
- The Swing wizard ITs drive a real UI via Jemmy and skip themselves when `GraphicsEnvironment` is headless.

### Bootstrapping gotcha

The root POM pins `truelicense-maven-plugin` to the **last released version (4.0.3)**, not `${project.version}` —
the project builds itself with its own previously published plugin. Changes to `maven-plugin/` or `build-tasks/`
therefore do not affect the current build until that version is released. The pinned plugin also carries an
explicit `plexus-utils` dependency because Maven 3.9+ stopped exporting `org.codehaus.plexus.util.*` to plugins.

## Module architecture

Runtime layering (each layer depends only on the ones above it):

| Module | Role |
| --- | --- |
| `api` | Interfaces and exceptions only — `LicenseManagementContext`, `Consumer`/`VendorLicenseManager`, `License`, plus sub-packages `auth`, `codec`, `crypto`, `passwd`, `i18n`, `x500`. No implementation. |
| `spi` | Small shared helpers for format modules (`Codecs`, resource-bundle-backed messages). |
| `core` | The **single** implementation of the whole API. `Core.builder()` returns a partially configured `LicenseManagementContextBuilder`. |
| `v1`, `v2-core`, `v2-json`, `v2-xml`, `v4` | License key *formats*. Each exposes a facade (`V1`, `V2Json`, `V2Xml`, `V4`) that calls `Core.builder()` and plugs in a codec factory, encryption, compression, keystore type, license factory and repository factory. |
| `ui` | Toolkit-agnostic wizard model/controller (`WizardModel`, `WizardController`, `LicenseWizardState`) and i18n messages. |
| `swing` | Swing views over `ui` (`LicenseManagementWizard`), plus `ConsumerLicenseManager` decorators that enable/disable UI. |
| `jsf` | JSF managed beans over `ui`. |
| `jax-rs` | `ConsumerLicenseManagementService` — exposes a consumer manager as a REST resource. |

Build-time-only modules: `obfuscate` (the `@Obfuscate` annotation + annotation processor + generated
`ObfuscatedString`), `build-tasks` (framework-agnostic ASM and Velocity tasks, plus a `ProGuardTask` that forks
ProGuard as an external process — it has no ProGuard compile dependency), `maven-plugin` (Mojos that wrap those
tasks). `tests` is integration tests only and is never installed or deployed.

### The core is one file

Almost all behaviour lives in `core/.../TrueLicenseManagementContext.java` as nested inner classes. Read it before
changing anything in `core`:

- `TrueConsumerLicenseManagerBuilder` / `TrueVendorLicenseManagerBuilder` — the fluent builders returned by
  `context.consumer()` / `context.vendor()`. Child builders (`authentication()`, `encryption()`) return to the
  parent via `up()`.
- `TrueLicenseManager` → `CachingLicenseManager` → `ChainedLicenseManager` is the decorator stack. `vendor()` builds
  the plain manager; `consumer()` builds `Caching`, or `Chained` when a `parent(...)` manager was configured.
  `ChainedLicenseManager` delegates to its parent first and falls back to the local store, generating a free trial
  period (FTP) key on the fly when `ftpDays` is set and no key is installed yet.
- `TrueLicenseInitialization` / `TrueLicenseValidation` supply default field values and the standard validation
  rules; user-supplied functions are merged with them via `LicenseFunctionComposition`.
- The clock is injected (`Clock`) and deliberately used instead of the system clock for issue/expiry checks.

### License key pipeline

Generate: `License` bean → duplicate via codec → initialize → validate → `RepositoryFactory.controller(...)` builds
a repository model → `Authentication` (`core.auth.Notary`, keystore-based) signs it → codec encode → compression →
encryption → `Store`.

Consume: the exact reverse — decrypt, decompress, decode the repository model, verify the signature, then decode
the `License` bean. `CachingLicenseManager` memoizes both the authenticated decoder and the decoded license per
source for `cachePeriodMillis`.

A *format* is therefore fully described by four pluggable pieces: `Codec`, `RepositoryFactory`/`RepositoryModel`,
`LicenseFactory`, and `Encryption` + keystore type. Compare `V1.java`, `V2Json.java` and `V4.java` — they differ
only in those choices. V1 additionally keeps the legacy `de.schlichtherle.license` / `de.schlichtherle.xml`
packages for binary compatibility with TrueLicense 1.x keys; V4 is the current format (JSON,
`PBEWithHmacSHA256AndAES_128`, PKCS12).

### Obfuscation

Constant `String` fields annotated `@Obfuscate` (algorithm names, keystore types, message keys) are rewritten in
the class files after compilation by `ObfuscateClassesTask` (ASM). Because `truelicense.obfuscate.scope` is `all`,
*every* constant string is rewritten, not only the annotated ones — the annotation matters only if the scope is
narrowed to `annotated`.

**The wiring is central and opt-out. Do not add `truelicense-maven-plugin` to a module POM.** The
`enable-obfuscate-main-classes` profile in the root POM activates on `src/main` and declares the plugin in
`<build><plugins>`, so every module with main sources is obfuscated automatically. A module opts *out* with two
properties, not by touching plugins:

```xml
<truelicense.obfuscate.scope>none</truelicense.obfuscate.scope>
<truelicense.obfuscate.verify.skip>true</truelicense.obfuscate.verify.skip>
```

`api` is the only module that does this, and it must: it deliberately has no dependency on `truelicense-obfuscate`,
so rewriting its constants would emit references to `ObfuscatedString` and fail with `NoClassDefFoundError` at
runtime. **That is the rule for any new module — obfuscation requires `truelicense-obfuscate` on the classpath.**
The root aggregator needs no opt-out; it has no classes and both the goal and the check no-op on a missing
`target/classes`.

**If a module must declare `truelicense-maven-plugin` for some other reason, it has to repeat the
`obfuscate-main-classes` execution.** A module-level `<plugin>` declaration *suppresses* the profile-injected
execution rather than merging with it — the effective POM ends up with only the module's own executions. This is
the same class of Maven merge behaviour that caused the 4.0.1 regression, and it is silent. `obfuscate` is the only
module in this position (it needs `generate-main-sources` / `generate-test-sources`), and its POM repeats the
execution with a comment saying why.

**Such a module must also spell the `<groupId>` exactly as the root POM's `<pluginManagement>` does — the literal
string, not just the same resolved value.** Maven keys a plugin to its management entry by the **raw,
un-interpolated** `groupId:artifactId`, so `global.namespace.truelicense` and `${project.groupId}` are *different*
keys even though both resolve identically. The mismatch is mostly cosmetic — `pluginManagement` injection happens
after interpolation, so the effective POM still gets the pinned version and the build works — but the raw-model
validator does not see it and warns on every run:

```
'build.plugins.plugin.version' for global.namespace.truelicense:truelicense-maven-plugin is missing.
```

The root POM writes `${project.groupId}` in both `<pluginManagement>` and the `enable-obfuscate-main-classes`
profile, so a module declaring the plugin must write `${project.groupId}` too. Confirmed by flipping the root POM
to the literal instead: the warning simply moved to the profile-injected declaration and then fired for *every*
module. Fixing the key also changes the plugin's **position** in the effective `build/plugins` list — in `obfuscate`
it moved after `maven-compiler-plugin`, i.e. into the module's own declaration order. Version and executions are
otherwise byte-identical, and the observed goal order is unaffected (`generate-main-sources` → `compile` →
`obfuscate-main-classes` → `generate-test-sources` → `testCompile`), because these plugins share no lifecycle phase.
Worth remembering only if a module ever binds two plugins to the *same* phase, where list order decides.

### Build-time verification

The `verify-obfuscate-main-classes` antrun execution runs at `prepare-package` and fails the module if its classes
carry no evidence of obfuscation. This exists because the failure mode is otherwise invisible — the goal simply
does not run and the build stays green.

**It is release-only.** It lives in a `sonatype-oss-release` profile, not in `enable-obfuscate-main-classes`, so an
ordinary `./mvnw install` does not pay for it. It still runs where it matters: `release:perform` activates that
profile (parent-pom sets `releaseProfiles` to it) and the CI compile job passes `--activate-profiles
sonatype-oss-release`. To run it by hand:

```bash
./mvnw install -P sonatype-oss-release -Dgpg.skip=true -Dmaven.javadoc.skip=true
```

That profile has no `src/main` activation, so the check also guards on `target/classes` existing and containing at
least one class file — that is what makes it a no-op for the root aggregator and `tests`.

The signal is a **method name synthesized by `ObfuscateClassesTask`**: `_clinit@…` from the `<clinit>` merger, or
`_string#N` from an obfuscated `ldc`. Both contain `@` / `#`, which are illegal in Java identifiers, so javac
cannot emit them — there are no false passes.

Do **not** weaken this to "references `ObfuscatedString`". Four modules — `obfuscate`, `build-tasks`, `core` and
`maven-plugin` — reference that class from their own source, so such a check stays green even when they are not
obfuscated at all. That is not hypothetical: it silently passed `obfuscate` during this work while the module was
in fact unobfuscated.

`v2-json` and `v2-xml` set `truelicense.obfuscate.verify.relaxed=true`. They obfuscate only *named* constants, so
the computation method inherits the field's name and no synthesized name appears; the weaker `ObfuscatedString`
check stands in, which is sound there because neither module's source references that class.

Verified by simulating a silent failure per module with
`./mvnw -pl <module> package -P sonatype-oss-release -Dtruelicense.obfuscate.scope=none`: every module fails,
including all four that reference `ObfuscatedString` in source. Only `api` passes, correctly, because it is opted
out.

Residual caveats: the check proves *something* in the module was obfuscated, not that a particular string was; and
it assumes the default `methodNameFormat` (`_%s#%d`) — overriding that property breaks the check, loudly.

### ASM version ceiling (affects applications, not this build)

`ObfuscateClassesTask` is an ASM bytecode rewriter, so it can only read class files up to the version its ASM
supports, and only use features up to its **API level**, which is hardcoded to `ASM7`. Measured directly with
`ClassReader`:

| ASM | shipped in | reads up to | rejects |
| --- | --- | --- | --- |
| 7.3.1 | `truelicense-maven-plugin` 4.0.3 (current release) | Java 15 | Java 16+ |
| 9.1 | pinned by `asm.version` on `develop` | Java 17 | Java 18+ |
| 9.8 | — | Java 25 | — |

Rejection is `IllegalArgumentException: Unsupported class file major version N`. This does **not** affect
TrueLicense's own build — it targets Java 8 bytecode (major 52) regardless of the JDK used. It affects
*applications* that run the plugin over their own classes: an app targeting Java 17 cannot use plugin 4.0.3.

Bumping `asm.version` alone is not enough. The `ASM7` API level in `ObfuscateClassesTask`, `O9nInitMethodVisitor`
and friends must be raised too — on ASM 9.1 with `api = ASM7`, reading a Java 17 **record** throws
`UnsupportedOperationException: Records requires ASM8`, so any app using records fails even on a new ASM.

### Why it is wired this way (two independent historical causes)

**Cause 1 — modules that never opted in.** Under the old opt-in design a module was obfuscated only if its POM
declared the plugin. `core`, `jax-rs`, `jsf`, `build-tasks` and `maven-plugin` never did, so they were never
obfuscated in *any* release. (Shipped `truelicense-core` does contain two classes referencing `ObfuscatedString` —
that is `ObfuscatedPasswordProtection` importing it in source, not evidence of obfuscation.) The opt-out wiring
above removes this failure mode entirely.

**Cause 2 — a Maven behaviour difference that hit the modules that *did* opt in.** These two causes are separate;
cause 1 cannot explain cause 2, because the relevant POMs are byte-identical across the tags. Reference counts in
the artifacts on Maven Central:

| module | declared the plugin | 4.0.0 | 4.0.1 |
| --- | --- | --- | --- |
| `spi`, `ui`, `v1`, `v4`, `swing` | yes | obfuscated | **0 — none** |
| `core` | no | 0 (2 source refs) | 0 (2 source refs) |
| `jax-rs`, `jsf` | no | 0 | 0 |

`truelicense-v4-4.0.0` is obfuscated; **`4.0.1` and `4.0.3` are not — no module is.** This was not a bad commit;
`git bisect` over `4.0.0..4.0.1` marks every commit good. It was a defect in the root `pom.xml` whose symptom
depended on the Maven version:

The profile used to declare the `obfuscate-main-classes` execution inside `<build><pluginManagement><plugins>`, and
each participating module declared a bare `<plugin>` with *no* `<executions>` element. `pluginManagement` **binds
nothing** on its own, and Maven 3.6.x does not merge a profile-injected `pluginManagement` execution into a plugin
declaration that has no `<executions>`. The goal never bound and the build succeeded silently. Maven 3.9.x does
perform that merge, which is the only reason it appeared to work.

Measured with the old wiring (`mvn -DskipTests package`, then `javap -p -c … | grep -c ObfuscatedString` on `V4`):

| Tree | Maven 3.6.3 | Maven 3.9.16 |
| --- | --- | --- |
| tag 4.0.0 | 0 refs, 1/11 modules | 6 refs |
| tag 4.0.1 | 0 refs, 1/11 modules | 6 refs |
| `develop` before the fix | 0 refs, 1/11 modules | 6 refs |

The one module that escaped did so by accident: `obfuscate` declares its own `<executions>`, which gave the model
merger something to merge into.

Three things now prevent a recurrence, and none should be undone casually: the execution no longer routes through
`pluginManagement`; `maven.enforcer.requireMavenVersion` is `[3.9.16,)` so an untested Maven cannot build a
release at all; and the antrun check above fails the build when obfuscation produces nothing.

Regardless, **verify the artifact rather than the log before publishing.** To check a whole module by hand, look
for the synthesized names — *not* for `ObfuscatedString`, which false-passes in `obfuscate`, `build-tasks`, `core`
and `maven-plugin`:

```bash
grep -rlaE '_clinit@|_string#' target/classes | wc -l   # 0 == the goal did not run
```

For a single class `javap` shows the difference directly: an obfuscated constant loses its field and gains a
synthesized method that builds an `ObfuscatedString` from a `long[]`, while an unobfuscated one still shows
`static final String X = "literal"` plus a plain `ldc`.

```bash
javap -p -c target/classes/<path>/Foo.class
```

`ObfuscatedString.java` and its spec are generated from `.vtl` Velocity templates by the plugin's
`generate-main-sources` / `generate-test-sources` goals — edit the `.vtl`, not the generated file.

## Integration tests (`tests/`)

Organised as reusable `*ITLike` / `*Spec` traits plus a per-format `V*TestContext` that mixes them in — adding
coverage usually means editing the shared trait, and adding a format means adding one `V*TestContext` and a thin
subclass per trait. `TestContext` builds vendor/consumer managers from keystore fixtures under
`src/test/resources/.../v1|v2/core|v4/` (all protected by the password `test1234`).

Note `tests/src/test/java` contains a `.scala` file (`ConfiguredTestContext.scala`) — scala-maven-plugin compiles
Scala across all test source roots, so this is intentional, not misplaced.

## Contributing

Pull requests require signing the CLA (`CLA.md`); a GitHub Action enforces it. Signatures are recorded under
`.cla/`.
