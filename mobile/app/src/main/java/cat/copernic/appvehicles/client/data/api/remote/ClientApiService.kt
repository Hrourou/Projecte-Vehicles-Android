//package cat.copernic.appvehicles.client.data.api.remote
//
//import cat.copernic.appvehicles.client.data.model.ClientProfileDto
//import cat.copernic.appvehicles.client.data.model.ClientUpdateRequest
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.GET
//import retrofit2.http.PUT
//import retrofit2.http.Path
//
//interface ClientApiService {
//
//    @GET("clients/{dni}")
//    suspend fun getClient(@Path("dni") dni: String): Response<ClientProfileDto>
//
//    @PUT("clients/{dni}")
//    suspend fun updateClient(
//        @Path("dni") dni: String,
//        @Body request: ClientUpdateRequest
//    ): Response<ClientProfileDto>
//}