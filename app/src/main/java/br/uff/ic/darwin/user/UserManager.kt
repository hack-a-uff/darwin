package br.uff.ic.darwin.user

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.Fuel
import kotlinx.coroutines.experimental.channels.Channel


class UserManager(
    val update: Channel<Student>
) {
    private val url: String = "http://10.1.198.107:8888"
    private val mapper : ObjectMapper = ObjectMapper()
    init {
        mapper.registerKotlinModule()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun getUser(cardId: String): Student {
        val response = Fuel.get("$url/v1/students/$cardId").response().second
        val src = String(response.data)
        return mapper.readValue(src)
    }
}