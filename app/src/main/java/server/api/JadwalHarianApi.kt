package server.api

class JadwalHarianApi {
    companion object{
        val BASE_URL = MainIpApi.MAIN_IP

        val GET_ALL = BASE_URL + "jadwalHarian"
        val GET_BY_DATE = BASE_URL + "jadwalHarianByDate/"
        val GET_KELAS_INSTRUKTUR_TODAY = BASE_URL + "jadwalKelasInstrukturToday/"
    }
}