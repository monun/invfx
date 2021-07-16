# InvFX

![Maven Central](https://img.shields.io/maven-central/v/io.github.monun/invfx)
![GitHub](https://img.shields.io/github/license/monun/invfx)
[![YouTube Channel Subscribers](https://img.shields.io/youtube/channel/subscribers/UCDrAR1OWC2MD4s0JLetN0MA?label=%EA%B0%81%EB%B3%84&style=social)](https://www.youtube.com/channel/UCDrAR1OWC2MD4s0JLetN0MA)

**Kotlin**으로 작성된 ***Bukkit(Spigot, Paper)*** 플랫폼의 **InventoryGUI** 라이브러리

---

### 기능

* InvWindow
* InvScene
* ListView
* Pane (Button)

---

### 환경

* JDK 16
* Kotlin 1.5.21
* Paper 1.17.1

### Gradle

```kotlin
repositories {
    mavenCentral()
}
```

```kotlin
dependencies {
    implementation("com.github.monun:invfx:Tag")
}
```

---

### Example code

```kotlin
InvFX.scene(5, "Example") {
    panel(0, 0, 9, 5) {
        listView(1, 1, 7, 3, false, "ABCDEFGHIJKLMNOPQRSTUVWXYZ".map { it.toString() }) {
            transform { item -> ItemStack(Material.BOOK).apply { lore(listOf(text(item))) } }
            onClickItem { _, _, _, item, event -> event.whoClicked.sendMessage(text("CLICK_ITEM $item")) }
        }.let { view ->
            button(0, 2) {
                onClick { _, _ -> view.page-- }
            }
            button(8, 2) {
                onClick { _, _ -> view.page++ }
            }
        }
    }
}
```
