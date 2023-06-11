package server.api

class IzinInstrukturApi {
    companion object{
        val BASE_URL = MainIpApi.MAIN_IP

        val GET_ALL = BASE_URL + "izinInstruktur"
        val ADD_DATA = BASE_URL + "izinInstruktur"
        val GET_BY_ID_INSTRUKTUR = BASE_URL + "izinInstruktur/byInstruktur/"
    }
}