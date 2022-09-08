# InvFX

[![Kotlin](https://img.shields.io/badge/java-17-ED8B00.svg?logo=java)](https://www.azul.com/)
[![Kotlin](https://img.shields.io/badge/kotlin-1.6.21-585DEF.svg?logo=kotlin)](http://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/gradle-7.4.2-02303A.svg?logo=gradle)](https://gradle.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.monun/invfx-core)](https://search.maven.org/artifact/io.github.monun/invfx-core)
[![GitHub](https://img.shields.io/github/license/monun/invfx)](https://www.gnu.org/licenses/gpl-3.0.html)
[![Kotlin](https://img.shields.io/badge/youtube-각별-red.svg?logo=youtube)](https://www.youtube.com/channel/UCDrAR1OWC2MD4s0JLetN0MA)

### Kotlin DSL for PaperMC Inventory GUI

---

* #### Features
    * Frame
        * Button
        * Pane
        * List

---

#### Gradle

```kotlin
repositories {
    mavenCentral()
}
```

```kotlin
dependencies {
    implementation("io.github.monun:invfx-api:<version>")
}
```

### plugins.yml

```yaml
name: ...
version: ...
main: ...
libraries:
  - io.github.monun:invfx-core:<version>
```
