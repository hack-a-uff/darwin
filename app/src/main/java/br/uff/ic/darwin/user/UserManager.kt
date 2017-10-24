package br.uff.ic.darwin.user

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.Fuel


object UserManager {
    private val url: String = "http://localhost:8888"
    private val mapper : ObjectMapper = ObjectMapper()
    init {
        mapper.registerKotlinModule()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun watchForUser() {

    }

    fun getUser(cardId: String): Student {
        val (_, response, _) =Fuel.get("$url/v1/students/$cardId").response()
        return mapper.readValue(response.data)
    }
}