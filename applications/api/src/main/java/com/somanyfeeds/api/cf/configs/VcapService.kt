package com.somanyfeeds.api.cf.configs

data class VcapService(
    val credentials: Map<String, Any>,
    val label: String,
    val name: String,
    val tags: List<String>
)
