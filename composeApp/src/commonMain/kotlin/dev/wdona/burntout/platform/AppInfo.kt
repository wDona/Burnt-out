package dev.wdona.burntout.platform

expect object AppInfo {
    // w.x.yz -> w. major version, x. centena de commits, yz. -> decena/ud de commit
    val version: String
}

