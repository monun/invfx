package com.github.noonmaru.winventory.plugin

import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Nemo
 */
class WinventoryPlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("Hello Kotlin Plugin!")
    }
}