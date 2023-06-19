package com.example.messenger.data.network

import com.example.messenger.common.GenericResponse
import com.example.messenger.data.network.models.chat.create.CreateChatResponse
import com.example.messenger.data.network.models.chat.list.ChatResponseItem
import com.example.messenger.data.network.models.message.MessageResponse
import com.example.messenger.data.network.models.search.SearchResponseItem
import com.example.messenger.data.network.models.signIn.dto.SignInDto
import com.example.messenger.data.network.models.taken.TokenResponse
import com.example.messenger.data.network.models.user.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/signIn")
    suspend fun signIn(
        @Body body: SignInDto
    ):GenericResponse<TokenResponse>

    @Multipart
    @POST("auth/signup")
    suspend fun signUp(
        @PartMap() partMap: @JvmSuppressWildcards Map<String, RequestBody>,
        @Part avatar: MultipartBody.Part?
    ):GenericResponse<TokenResponse>

    @GET("auth/logout")
    suspend fun logout():GenericResponse<Unit>

    @GET("user/my")
    suspend fun getMyProfile():GenericResponse<UserResponse>

    @GET("user/search")
    suspend fun makeSearch(
        @Query("query") query:String,
        @Query("size") size:Int?,
        @Query("page") page:Int?
    ):GenericResponse<List<SearchResponseItem>>

    @FormUrlEncoded
    @POST("chat")
    suspend fun createChat(
        @Field("friendUserId") friendId:String
    ):GenericResponse<CreateChatResponse>

    @DELETE("chat/{id}")
    suspend fun deleteChat(
        @Path("id") id:String
    ):GenericResponse<Unit>

    @GET("chat")
    suspend fun getChats(
        @Query("size") size:Int?,
        @Query("page") page:Int?
    ):GenericResponse<List<ChatResponseItem>>

    @Multipart
    @POST("chat/message")
    suspend fun sendMessage(
        @PartMap() partMap: @JvmSuppressWildcards Map<String, RequestBody>,
        @Part images: List<MultipartBody.Part?>
    ):GenericResponse<MessageResponse>

    @GET("chat/messages/{chatId}")
    suspend fun getAllMessages(
        @Path("chatId") chatId:String,
        @Query("size") size:Int?,
        @Query("page") page:Int?
    ):GenericResponse<List<MessageResponse>>
}
