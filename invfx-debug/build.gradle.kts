/*
 * InvFX
 * Copyright (C) 2021 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

repositories {
    mavenLocal()
}

val projectAPI = project(":${rootProject.name}-api")
val projectCore = project(":${rootProject.name}-core")

dependencies {
    implementation(projectAPI)
}

val pluginName = "InvFX"
extra.set("pluginName", pluginName)

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    create<Jar>("debugMojangJar") {
        archiveBaseName.set(pluginName)
        archiveVersion.set("")
        archiveClassifier.set("MOJANG")

        from(project.sourceSets["main"].output)
        (listOf(projectAPI, project, projectCore) + projectCore.subprojects).forEach {
            from(it.sourceSets["main"].output)
        }

        exclude("paper-plugin.yml")
        rename("mojang-plugin.yml", "plugin.yml")

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug-mojang/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }

    create<Jar>("debugPaperJar") {
        dependsOn(projectAPI.tasks.named("publishAllPublicationsToDebugRepository"))
        dependsOn(projectCore.tasks.named("publishAllPublicationsToDebugRepository"))

        archiveBaseName.set(pluginName)
        archiveVersion.set("")
        archiveClassifier.set("PAPER")

        from(project.sourceSets["main"].output)
        exclude("mojang-plugin.yml")
        rename("paper-plugin.yml", "plugin.yml")

        doLast {
            copy {
                from(archiveFile)
                val plugins = File(rootDir, ".debug-paper/plugins/")
                into(if (File(plugins, archiveFileName.get()).exists()) File(plugins, "update") else plugins)
            }
        }
    }
}
