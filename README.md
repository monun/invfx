# InvFX
[![Build Status](https://travis-ci.com/monun/invfx.svg?branch=master)](https://travis-ci.org/monun/invfx)
[![JitPack](https://jitpack.io/v/monun/invfx.svg)](https://jitpack.io/#monun/invfx)
![GitHub](https://img.shields.io/github/license/monun/invfx)

**Kotlin**으로 작성된 ***Bukkit(Spigot, Paper)*** 플랫폼의 **InventoryGUI** 라이브러리

---
### 기능
* InvWindow
* InvScene
* ListView
* Pane (Button)
---
### 환경
* JDK 8
* Kotlin 1.5.10
* Paper 1.16.5
### Gradle
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
```groovy
dependencies {
    implementation 'com.github.monun:invfx:Tag'
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
