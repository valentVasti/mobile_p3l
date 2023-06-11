package server.api

class PresensiInstrukturApi {
    companion object{
        val BASE_URL = MainIpApi.MAIN_IP

        val UPDATE_JAM_MULAI = BASE_URL + "presensiInstruktur"
        val UPDATE_JAM_SELESAI = BASE_URL + "presensiInstruktur/updateJamSelesai/"
        val CHECK_UPDATE_JAM_MULAI = BASE_URL + "presensiInstruktur/checkUpdateJamMulai/"
    }
}