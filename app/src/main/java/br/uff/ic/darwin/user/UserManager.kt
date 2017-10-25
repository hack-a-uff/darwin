package br.uff.ic.darwin.user

import br.uff.ic.darwin.ACTUALSTUDENT
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.Fuel
import kotlinx.coroutines.experimental.channels.Channel


class UserManager<Chan>(
    val update: Channel<Chan>
) {
    private val url: String = "http://10.1.198.107:8878"
    private val mapper : ObjectMapper = ObjectMapper()
    init {
        mapper.registerKotlinModule()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun getUser(cardId: String): Student {
        val response = Fuel.get("$url/v1/students/$cardId").response()
        val src = String(response.second.data)
        val aux = mapper.readValue<Student>(src)
        ACTUALSTUDENT = aux
        return aux
    }

    fun updateFunds(cardId: String, funds:Double){
        val response = Fuel.patch("$url/v1/students/$cardId").header("Content-Type" to "application/json")
            .body(mapper.writeValueAsBytes(mapOf("uffFunds" to funds)))
            .response()
    }
    fun getContacts(cardId: String): List<Student>{
        val response = Fuel.get("$url/v1/students/$cardId/contacts").response()
        val srt = String(response.second.data)
        val list = mapper.readValue<List<Student>>(srt)
        return  list
    }

    fun addContact(studentID: String, contactID:String): List<Student>{
        val response = Fuel.post("$url/v1/students/$studentID/contacts/$contactID").response().second
        return listOf()
    }
}