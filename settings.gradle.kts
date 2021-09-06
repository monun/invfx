rootProject.name = "invfx"

val prefix = "invfx"
val core = "$prefix-core"

include(
    "$prefix-api",
    "$prefix-core",
    "$prefix-debug"
)