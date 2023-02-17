# InvFX

[![Kotlin](https://img.shields.io/badge/java-17-ED8B00.svg?logo=java)](https://www.azul.com/)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7.21-585DEF.svg?logo=kotlin)](http://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/gradle-7.6-02303A.svg?logo=gradle)](https://gradle.org)
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

### Example
```kotlin
val strings = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList().map { it.toString() }

InvFX.frame(6, text("TEST")) {
    // 목록
    list(1, 1, 3, 3, true, { strings }) {
        // strings를 아이템으로 변환
        transform { s ->
            ItemStack(Material.BOOK).apply {
                editMeta { it.displayName(text(s)) }
            }
        }
        // 클릭시 아이템 정보와 좌표 출력
        onClickItem { x, y, item, _ ->
            println("$x $y $item")
        }
    }.let { list ->
        // 페이지 이동 버튼
        slot(0, 1) {
            item = ItemStack(Material.SLIME_BALL)
            onClick {
                list.page -= 1
            }
        }
        slot(4, 1) {
            item = ItemStack(Material.MAGMA_CREAM)
            onClick {
                list.page += 1
            }
        }
    }
    
    // 상대좌표로 지정이 가능한 클래스
    pane(5, 0, 7, 2) {
        // pane 내의 1, 1 좌표에 아이템 설정
        item(1, 1, ItemStack(Material.EMERALD))

        onClick { x, y, _ ->
            println("$x $y ${item(x, y)}")
        }
    }

}.let {
    player.openFrame(it)
}

```