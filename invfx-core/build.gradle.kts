dependencies {
    api(projectApi)
}

tasks {
    jar {
        archiveClassifier.set("origin")
    }

    register<Jar>("coreReobfJar") {
        from(sourceSets["main"].output)
    }
}