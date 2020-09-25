# InvFX
[![Build Status](https://travis-ci.org/noonmaru/invfx.svg?branch=master)](https://travis-ci.org/noonmaru/invfx)
![JitPack](https://img.shields.io/jitpack/v/github/noonmaru/invfx)
![GitHub](https://img.shields.io/github/license/noonmaru/invfx)

**Kotlin**으로 작성된 ***Bukkit(Spigot, Paper)*** 플랫폼의 **InventoryGUI** 라이브러리

> ---
> ### Features
> * InvWindow
> * InvScene
>     * Pane (Button)
>    * ListView
> ---
> ### Gradle
>```groovy
>allprojects {
>       repositories {
>        ...
>        maven { url 'https://jitpack.io' }
>    }
>}
>```
>```groovy
>dependencies {
>    	implementation 'com.github.noonmaru:invfx:Tag'
>    }
>```
>---
 ### Example code
```kotlin
InvFX.scene(5, "Example") {
    panel(0, 0, 9, 5) {
        listView(1, 1, 7, 3, false, "ABCDEFGHIJKLMNOPQRSTUVWXYZ".map { it.toString() }) {
            transform { item -> ItemStack(Material.BOOK).apply { lore = listOf(item) } }
            onClickItem { _, _, _, item, _ -> Bukkit.broadcastMessage("CLICK_ITEM $item") }
            onUpdateItems { _, _, displayList -> Bukkit.broadcastMessage("UPDATE $displayList") }
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
