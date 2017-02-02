package com.somanyfeeds.cloudfoundry

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

data class VcapService(
    val credentials: Map<String, Any>,
    val label: String,
    val name: String,
    val tags: List<String>
)

fun readVcapServices(getenv: (String) -> String? = System::getenv): List<VcapService> {
    val mapType = object : TypeReference<Map<String, List<VcapService>>>() {}
    val objectMapper = ObjectMapper().apply { registerKotlinModule() }

    val servicesJson = getenv("VCAP_SERVICES")!!
    val serviceMap: Map<String, List<VcapService>> = objectMapper.readValue(servicesJson, mapType)

    return serviceMap.values.flatten().toList()
}
