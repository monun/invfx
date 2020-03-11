# InvFX
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
>    	implementation 'com.github.noonmaru:inv-fx:1.0'
>    }
>```
 ### Example code
```kotlin
val scene = invScene(6, "Example") {
    val listView = addListView(0, 0, 9, 5, list) {
        onInit = { println("ListView INIT") }
        onClickItem = { listView, x, y, item, event ->
            println("ListView Click Item $item")
        }
    }

    addPanel(0, 5, 9, 1) {
        addButton(0, 0) {
            item = ItemStack(Material.STICK)
            onClick = { button, event ->
                listView.first()
            }
        }
        addButton(8, 0) {
            item = ItemStack(Material.BLAZE_ROD)
            onClick = { button, event ->
                listView.last()
            }
        }
        addButton(3, 0) {
            item = ItemStack(Material.STONE)
            onClick = { button, event ->
                listView.previous()
            }
        }
        addButton(5, 0) {
            item = ItemStack(Material.GRASS_BLOCK)
            onClick = { button, event ->
                listView.next()
            }
        }
    }
}
```
