package br.uff.ic.darwin

import com.github.kittinunf.fuel.Fuel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

public object requester {

    public fun get(url: String, params:Map<String, Any>):String{

        return runBlocking {
            var input = Channel<String>(1)
            launch(CommonPool){
                val (request, response, result) = Fuel.post(url, params.toList()).authenticate("sk_test_xS2OT9tcD7tFxurwdmnDY7vb", "").response()
                if (response.statusCode in 200..299) {
                    input.offer(String(response.data))
                } else {
                    input.offer("deu ruim")
                }
            }
            input.receive()
        }
    }
}


