package io.damo.dropwizard.cloudfoundry

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.configuration.ConfigurationFactoryFactory
import javax.validation.Validator

class CloudFoundryConfigurationFactoryFactory<T>(val mapper: (List<VcapService>) -> (T), val getenv: (String) -> String? = System::getenv) : ConfigurationFactoryFactory<T> {

    private val mapType = object : TypeReference<Map<String, List<VcapService>>>() {}
    private val objectMapper = ObjectMapper().apply { registerKotlinModule() }


    override fun create(klass: Class<T>, validator: Validator, objectMapper: ObjectMapper, propertyPrefix: String)
        = create()

    fun create(): ConfigurationFactory<T> {
        val servicesJson = getenv("VCAP_SERVICES")!!
        val serviceMap: Map<String, List<VcapService>> = objectMapper.readValue(servicesJson, mapType)
        val serviceList = serviceMap.values.flatten().toList()

        return CloudFoundryConfigurationFactory(mapper, serviceList)
    }
}
