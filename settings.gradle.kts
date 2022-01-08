rootProject.name = "invfx"

val api = "${rootProject.name}-api"
val core = "${rootProject.name}-core"
val debug = "${rootProject.name}-plugin"

include(api, core, debug)
include("${rootProject.name}-publish")