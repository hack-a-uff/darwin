package br.uff.ic.darwin.user

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.Fuel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit


class UserManager(
    val update: Channel<Student>
) {
    private val url: String = "http://localhost:8888"
    private val mapper : ObjectMapper = ObjectMapper()
    init {
        mapper.registerKotlinModule()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        launch(CommonPool) {
            delay(2,TimeUnit.SECONDS)
            watchForUser()
        }
    }

    suspend fun watchForUser() {
        update.send(Student(
            name = "Pattun",
            cardNfcId = "1234",
            course = "Be A Duke",
            expiresAt = "today",
            picture = "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAYAAABccqhmAAAR8ElEQVR4nO3df2xd9XnH8XeNZ3lW" +
                "5qVRcLMMpZmXZsxLM+atLM0gYgxFFAU0ITqhDFWUSq1UZRPrEJsiBkL5AyFUTVW3obRljKFutIwF" +
                "ioDSloSIZcyFhEJgNCVJCSQhIb+an4Q4TvbHc604nu17zz3fc57v95zPS3p0Hcnxfe655/meX98f" +
                "ICK19RHvBKRWuoG5jZgO9DSiGzgJHANONF73A1uBgw551oYaAClKD7AYWAT8EXAJMKuNv3MQawhe" +
                "B9YBa4HdgXIUkYCmATcCjwHHgbMFxRbgHmB+OR9LRKZyEXAfcIjiin6y2ADcBHQU/ilF5DwzgdXA" +
                "MOUX/kRnBWoIRErQAazA54jfLDYCC4v76CL11gf8EP9Cnyo+BO4EOgvaBiK1tBjYhX+Btxo/BGYU" +
                "siVEauZqir2zX1T8HBgoYHuI1Mb12Gm1dzG3G/uAweBbRaQGFgEf4F/EeeMQcGngbSNSaXOBvfgX" +
                "b8gzgXkhN5BIVXUAL+JftKFjC9Z/QUSmcDv+xVpUbMAGIInIBOaT9k2/VuLRYFsrYRd4JyBR+gaw" +
                "wDuJgg1gXZhf8E5EJCaX4n90LitGgKvCbDaRangS/8IsM/ZioxlFam8OdlT0Lsqy40WgK8D2S46G" +
                "TspYX6Ke+8Qi4H7vJES87cD/aOwZX8m/CUXStBD/AvSOEWzQU23U8XRPJrbMO4EIdADfQROKSA2t" +
                "x/8IHEvsQ42A1EgHcBT/wospDqA+AlITA/gXXNYYAd4AnsPmASxispIR4G40bkAq7ib8C7rVOAys" +
                "xOYmHKsH+Bw2A1Do99yBrXcgUkl34l/YrcQWmo/n7wWeLej9XwSWopvnUjEP4F/czeIA0N/i5+kB" +
                "Xikwl3eBe/Gba3AG1nlpEbbGokguz+Ff4M3i1oyfqaxBTa8CXweWY12pQ5uDzcl4D/Y9HZgghzW0" +
                "OcmJFgcVsKPlJd5JTOEkcCG2anAWQ5Q/D+BubCHTd7B7BzsbcQQ4DZxqxGnsTKW3EdOwIv4N7Exn" +
                "NFo9wv8H8NlQH0LqZQv+R/ipYkObn+veCHIvK0awhiQT3cwQiP8xV7vLge8KmkXcOrCziMz/SST2" +
                "BiDzkS3n/0vRaewyIxM1AAJ2jR2zBbS3r/5u6EQitons90jUAAjQxo5TstnAFRn/zwzqNbJPk5xK" +
                "217C/yZWsxgi22q/X4sg57LiEPW63JHAUugHcBabtaeVs9abI8i1zLithW0iMqkUegKOxpNM3uGm" +
                "F/j7CHIsM4bIcSmf5ZRKqmubdwIZLMOG6f4Am9N/N3a9/yngOurVNfYg8OfAGe9EJG034n8kU2SL" +
                "YQLMV6CnAALWdVXS8lfAj7yTkGrowMbZex/VFK1FsCnMdQYgYNeQP/ZOQlryCPAXof6YGgAZpUUy" +
                "4/cv2E2/0855SAUN4n9qq5g4RoBV6IAtBXsX/51dcX4cAK6Z6kvLQy2KjPW4dwJynrXA7wNPeyci" +
                "9aDLgDjiPWyKMZHSDeFfAHWND7GuzBrYI25uwb8Q6hZHga9iw55FXPVgw0u9i6IOsQ+4AxvLIBKN" +
                "r+NfHFWNYeApbPxF7FOxSU0twL9QqhavYGsbzMrwPYi4eQb/okk5hrHelSuxBlUkKUvwL6LUYgew" +
                "GlvJJ4k7+VoZSKayAVjsnUSkTgH/C/wEm1NxLfBT14zaoAZAprIMm4Krzk4AbzdiO7YW4CZsDoVT" +
                "blkFogZAmtlMta9hj3GuwN/GTuPH/nu/R1JlUQMgzdwEPOydRCCnsHkPXsZO218GtqI59UQm1Un6" +
                "owQ3YlOF12nCUJFgbse/iNuJ97A78iKSw3Ssv7p3QWeJIdTpRiSYlJba2oL614sENRfr3eZd3M1i" +
                "GFhYzCYQqbdH8S/wZhFsymwROd9S/Au8Wcwv7NOL1FwHdmfdu8gni1eK++jVpElBJYszwHe9k5jC" +
                "970TSI0aAMnq370TmMKr3gmI1ME2/E/3J4oqj1kohM4ApB2PeCcwid3eCYjUwQD+R/uJoqvIDy0i" +
                "58R2GfBBsR+3mnQJIO163juBcbQvt0EbTdq1zjuBcbpIZB6+mKgBkHY9753ABPq8E0iNGgBp105g" +
                "j3cS48z0TiA1agAkj9e8ExhHZwAZqQGQPGJrAOZ5J5AaNQCSx2bvBMa53DuB1KgBkDx+5p3AOJd5" +
                "J5AaNQCSR2xdb2cCF3snkRI1AJLHbuKbU19nARmoAZA8TgPveycxzrXeCYjUyUb8xwGMjWE0HXjL" +
                "dAYgeR3zTmCcTmw5M2mBGgDJ64R3AhP4vHcCqVADIHmd9E5gAgPAIu8kUqAGQPKKsQEA+GvvBFKg" +
                "BkDyirUBuB7NEdiUGgDJq9M7gUl0AH/nnUTs1ABIXtO8E5jCDdj9AJmEGgDJK+YGoAO4yzsJkSp7" +
                "Cf/OP81C3YNFCrIP/wJvFq8S770KkWT14l/crcaKgraBSG0N4l/YrcYhNGXY/6ObgJJHSs/ZpwP3" +
                "eSchUiWr8T+yZ41rCtkSIjW0Gf+Czhq7sLMBEclhOjCCf0G3Ew8WsD1EamU5/oWcJ64Ov0lE6uNR" +
                "/Is4T+xCKwmJtKUbOIp/EeeNNaE3jEgd3IB/8YaKLwbeNiKV9yz+hRsqjgPzw24ekerqJ927/5PF" +
                "RqAr5EZKhXoCSlZfonr7zSCwyjsJkdj1Yn3qvY/YRcQIcGW4TSVSPXfgX6hFxrvAjGBbS6RCeoED" +
                "+Bdp0fFYqA0mUiV341+cZcUtgbaZSCXMxR6XeRdmWXG48ZlFBDst9i7KsmM91XvaIZLZVfgXo1fc" +
                "GmD7iSRrGrAN/0L0ig+Ai3NvRZFE3Y9/EXrHS2hGYamhpfgXXyxxZ85tKZKU2cB7+BdeLDGMdRcW" +
                "qbxu0ljtp+x4o7FtKuUC7wQkOg+i6bImciHQA/zAOxGRotyG/5E25hgBFre9dUUitpTqjfMvIiq1" +
                "zqAuAQRsRpxngV/2TiQBH8MGRQ15JyISwkzgTfyPrCnFYWBWOxtbJCZ92N1t74JKMR5uY3uLRGMW" +
                "Kv68sSTzVheJwCx02h8iNmTd8CLeZgNb8C+eqoRWG5ZkqPjDxyuZvgERJ/3AW/gXTBXjhgzfg0jp" +
                "rqQeE3p6xZto9iCJ1ApsNJt3kVQ9dBYgUekCvol/YdQl1rX2tYgUrw94Af+iqFssaOXLiYnGAlTP" +
                "IPAc8EnvRGqoA3jKOwmprxXUa/7+2OIotoKSSKnmYEd97wJQWCMsUppbsNFp3ju+wmL91F+XSBiz" +
                "setN7x1ecX4MY0Osk6DOC2laDmxG/dBj1Aks805CqqkfWIP/UU4xdayZ7AsUaUcvcC+2VJX3zq1o" +
                "HsexGYRFcukAvgjsxX+nVmSLJC4DdA8gXldiQ01XYz37JC2f8k6gFWoA4jMfeAJ7rr/QORdp3yXe" +
                "CUhaBoCH0Mi9qsQORFqwCDvia1GO6sUMRCZxNdZrzHsnVRQXVxI53QMoVwdwI3Zz7xk0rXTVRX8P" +
                "pzJrnEVuDnAz8HlgrmsmUqaPeSfQjBqA4nQDfwp8ATsV1NlW/Uz3TqAZNQDhDWJFv5wEdgApVPTf" +
                "vxqAMPqB67BT/Oiv+6Q0agAqqhN7fHct1uVzwDcdiZQagAqZjj26u7bxqme80owagITNAC4FPg1c" +
                "ASxG20uy6fZOoBnt0KYTu3ZfBPxh43UeunMv+Zz0TqCZujUAHdgz+X6swH8LO8oPovHbEt4J7wSa" +
                "8WwA+rDVa04BpxuvJ4Azbf69LmzijGnYtddoof8mVuz9WCecrjxJi2SgBmCM6djkFp/BVlCZbOLE" +
                "0YZgsujkXKGPfVVhS2zUADRcDzxIa4smdDUi+juoIk3s8U6gmTIagEHg2yRwR1QksHe8E2imjLvc" +
                "n0XFL/UU/aQgZTQAs0p4D5EY6QwAGCrhPURitN07gWY+UsJ7zATeJKHlkkQCOAJ8lPYfa5eijDOA" +
                "/cDflPA+IjHZROTFD+V1df1n4J9Kei+RGLzsnUArQj0GnIHNfvNprNfd6On+KexGyHZsnvtpwOcC" +
                "vadIzF7yTqAVee8B9AGrsDXqW2lMzqABNlIPHyeBpwB5zgDmYtNaz8nwf1T8Ugevk0DxQ76CfJhs" +
                "xS9SF9/zTqBV7TYAFwOXhUxEpEKe9E6gVe02AJoOS2Ri7wM/9k6iVe02ANGPchJx8j0SeP4/qt0G" +
                "YDsJtXIiJVrtnUAWeW4CfjVYFiLV8DKJdAAalacB+C7wn6ESEamAf/ROIKsQHYGG0IKXIr8Afp0E" +
                "pgEbK2/HnPeBPwF2BshFJGXfIrHih3DDgecD64DZgf6eSEqOYLNP7/dOJKtQXXN/hi2ooScDUkf3" +
                "kmDxQ/gJQbqB+4GbA/9dkVjtBj5Bgqf/ABcE/nungSeAfcAfA78U+O+LxOY24H+8k2hXkVOC9WNr" +
                "ASwp8D1EPL0O/B524EtS6DOAsQ4B/4pdGy1BK/dItZwCrsEuAZJV9Pj8M8A/AJ8EHi/4vUTKdBfw" +
                "E+8k8ipjVuCxrsC6EA+W/L4iIf03cDkJDfqZTJGXABN5G+sw8Ta2LPevlPz+InkdA5YCB70TCaHs" +
                "BgDgLHbq9A3gQ+wmipYOk1R8AZsKTwLpBVZijw7PKhQRxx1IYaYBtwN78f+iFYrx8U2kFD3AV4Bd" +
                "+H/pCsVZ4BnCraEhLeoCbgI24r8DKOobG7GzU3G0BFgDjOC/QyjqE0NoUduo9ANfA47iv3Moqh1P" +
                "YZejEqFe7D7BNvx3FEX14gF0zZ+EDuAq4FGsT4H3jqNIP1YhSerDHiO+hf9OpEgvjmOL3EoFXAV8" +
                "B50VKFqLjdhyd1Ixo2cFb+C/kyniixHgHjRUvRYWYl/2z/Hf8RT+sQNNVlNbi7FFHNTtuH4xjHXr" +
                "nY7UXidwNfAQcBj/nVNRbDwJDCAygW7geuBhNDKxajGETUYj0pIO7DLhHmAz/juwor3YBvwZIjnN" +
                "Bb6MjQr7AP8dWzF1vAAsR3f3pQA9wHXYjSQNWY4nDmM3dhdM/tXJeGVPClpF87DHSZcDlzX+LeV5" +
                "DSv8f8Pm65MM1ACEN4vzG4SFFD/9et28BjyNDRHXepQ5qAEoXi/WEIw2CH+AJkHN6iSwFnuM9zTw" +
                "jm861aEGoHwd2GXCQmzBlAWNn/vRmcKoM8BW4Hms6NeS6OKbsVMDEI8erJPKAqxhWNj4eZZnUiU4" +
                "gy0vvwkblLOpEUc8k6oLNQDxmwm8SPo3F08De4Cd2NF9bLHr5p0TzXoSv/3AI7Q/J/072KVFLza5" +
                "ZejLjJONOIItlLmz8Z67Gq87G7GHhFfRrSqdAaThCmBdG/9vD/BxbCVbsOKfhjUGvdjNyC7sQNA5" +
                "5ueuxu+ewIp7steTVGB9PJHYddNeD8S7PZIVkfA2kL0BWOySqSRDj53S8dOMv38MdZKRJtQApOOt" +
                "jL//X+immzShBiAd+zP+/vZCspBKUQOQjqxH84OFZCGVogYgHVm/q0OFZCGVogYgHRdl/P2PFpKF" +
                "VIoagHR8IuPvzy4kCxFx8S7Z+gBs9ElTREJbRHvTZOksQKQCnqC9BmClR7IiEs4AtoZdOw3AXmw4" +
                "cau6sBVzpmODhrrRiNFK02jA+D2DrV7Urk1Yp6AerKB7xv089nWqm8KnJ4lTWJ+D/eNeDzR+3o8N" +
                "C97KuVGJItKCv8R/uu1QMYzdmLw06BYSqahLqN4iJCPYDU0RmUIftny1d8GGjMPYgioiMoVObGkr" +
                "74INGXvRqrwiLVmJf8GGjvuCbiGRippH9a77zwLvkf6sxiKFux//Yi0qjgOrUO9EkUltwb9Qi45h" +
                "YD1wK7bsuog07MO/QMuODcCXsZ6HIrU2hH9BesUWrCuylEjzAcTlAe8EHO1Hk5hKzXUAz+F/NC4z" +
                "3gBuRoOORABbsms9/oVZdKwDrgm0zUQqpRO4nWr2CXgMGAy3qUSqaz7wLP5FGyL2AUvDbh6RelgI" +
                "PAR8iH8htxsaBhyhC7wTkJbsBR4HvoUdSX8V+DXSmtDlLPAjdKdfJIiLgBXYU4Pj+B/hW4lX0ahA" +
                "keC6sKXA/xabQuwQ/sU+WRwHlhWzGSSrlE4hJZuLsKPtxcDvNF4HyDZJaFG+D3zGOwlRA1BHfZxr" +
                "DH4bG5DTNyaK7JO/Gyv+u4CdBb6PtEgNgIzXzfkNQh921nAh56YJ78R6LXZO8O8zwC+AI9g0YLux" +
                "WYm3YrMDi4hIDP4PZbUFwyS4j8kAAAAASUVORK5CYII=",
            rioCardFunds = 120.0,
            uffFunds = 125.0,
            uffRegistrationNumber = "3334253477"
        ))
    }

    fun getUser(cardId: String): Student {
        val (_, response, _) =Fuel.get("$url/v1/students/$cardId").response()
        return mapper.readValue(response.data)
    }
}