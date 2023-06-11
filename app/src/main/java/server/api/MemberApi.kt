package server.api

class MemberApi {
    companion object{
        val BASE_URL = MainIpApi.MAIN_IP

        val GET_ALL = BASE_URL + "member"
        val GET_BY_ID = BASE_URL + "member/"
    }
}