package com.somanyfeeds.api.cf

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.somanyfeeds.api.cf.configs.VcapService
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.configuration.ConfigurationFactoryFactory
import javax.validation.Validator

class CloudFoundryConfigurationFactoryFactory<T>(val mapper: (List<VcapService>) -> (T), val getenv: (String) -> String? = System::getenv) : ConfigurationFactoryFactory<T> {

    private val mapType = object : TypeReference<Map<String, List<VcapService>>>() {}

    override fun create(klass: Class<T>, validator: Validator, objectMapper: ObjectMapper, propertyPrefix: String): ConfigurationFactory<T> {
        return create(objectMapper)
    }

    fun create(objectMapper: ObjectMapper = ObjectMapper()): ConfigurationFactory<T> {
        objectMapper.registerKotlinModule()

        val servicesJson = getenv("VCAP_SERVICES")!!
        val serviceMap: Map<String, List<VcapService>> = objectMapper.readValue(servicesJson, mapType)
        val serviceList = serviceMap.values.flatten().toList()

        return CloudFoundryConfigurationFactory(mapper, serviceList)
    }
}
