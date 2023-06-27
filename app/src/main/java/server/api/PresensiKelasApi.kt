package server.api

class PresensiKelasApi {
    companion object{
        val BASE_URL = MainIpApi.MAIN_IP

        val ADD_DATA = BASE_URL + "presensiKelas"
        val UPDATE_DEPOSIT_MEMBER = BASE_URL + "updateDepositMember/"
    }
}