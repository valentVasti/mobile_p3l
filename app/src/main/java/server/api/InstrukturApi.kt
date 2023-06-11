package server.api

class InstrukturApi {
    companion object{
        val BASE_URL = MainIpApi.MAIN_IP
        val GET_ALL = BASE_URL + "instruktur"
        val GET_BY_ID = BASE_URL + "instruktur/"
    }
}