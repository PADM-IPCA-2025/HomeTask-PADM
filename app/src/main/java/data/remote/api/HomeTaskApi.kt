package pt.ipca.hometask.data.remote.api

import pt.ipca.hometask.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface HomeTaskApi {

    // Home endpoints
    @POST("api/tasks/homes")
    suspend fun createHome(@Body home: HomeDto): Response<HomeDto>

    @GET("api/tasks/homes")
    suspend fun getAllHomes(): Response<List<HomeDto>>

    @GET("api/tasks/homes/{id}")
    suspend fun getHomeById(@Path("id") id: Int): Response<HomeDto>

    @GET("api/tasks/homes/user/{id}/all")
    suspend fun getHomeByUserId(@Path("id") id: Int): Response<ApiResponse<List<HomeDto>>>

    @PUT("api/tasks/homes/{id}")
    suspend fun updateHome(@Path("id") id: Int, @Body home: HomeDto): Response<HomeDto>

    @DELETE("api/tasks/homes/{id}")
    suspend fun deleteHome(@Path("id") id: Int): Response<Unit>

    // Task endpoints
    @POST("api/tasks/tasks")
    suspend fun createTask(@Body task: TaskDto): Response<TaskDto>

    @GET("api/tasks/tasks")
    suspend fun getAllTasks(): Response<List<TaskDto>>

    @GET("api/tasks/tasks/{id}")
    suspend fun getTaskById(@Path("id") id: Int): Response<TaskDto>

    @GET("api/tasks/tasks/home/{homeId}")
    suspend fun getTasksByHome(@Path("homeId") homeId: Int): Response<ApiResponse<List<TaskDto>>>

    @GET("api/tasks/user/{userId}")
    suspend fun getTasksByUser(@Path("userId") userId: Int): Response<ApiResponse<List<TaskDto>>>

    @PUT("api/tasks/tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: TaskDto): Response<TaskDto>

    @DELETE("api/tasks/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Unit>

    // Task Category endpoints
    @POST("api/tasks/task-categories")
    suspend fun createTaskCategory(@Body category: TaskCategoryDto): Response<TaskCategoryDto>

    @GET("api/tasks/task-categories")
    suspend fun getAllTaskCategories(): Response<List<TaskCategoryDto>>

    @GET("api/tasks/task-categories/{id}")
    suspend fun getTaskCategoryById(@Path("id") id: Int): Response<TaskCategoryDto>

    @PUT("api/tasks/task-categories/{id}")
    suspend fun updateTaskCategory(@Path("id") id: Int, @Body category: TaskCategoryDto): Response<TaskCategoryDto>

    @DELETE("api/tasks/task-categories/{id}")
    suspend fun deleteTaskCategory(@Path("id") id: Int): Response<Unit>

    // Task Participants endpoints
    @POST("api/tasks/task-participants")
    suspend fun createTaskParticipant(@Body participant: TaskParticipantDto): Response<Unit>

    @GET("api/tasks/task-participants/user/{userId}")
    suspend fun getTaskParticipantsByUserId(@Path("userId") userId: Int): Response<List<TaskDto>>

    @GET("api/tasks/task-participants/task/{taskId}")
    suspend fun getTaskParticipantsByTaskId(@Path("taskId") taskId: Int): Response<List<UserDto>>

    @DELETE("api/tasks/task-participants/{userId}/{taskId}")
    suspend fun deleteTaskParticipant(@Path("userId") userId: Int, @Path("taskId") taskId: Int): Response<Unit>

    // Residents endpoints
    @POST("api/tasks/residents")
    suspend fun createResident(@Body resident: ResidentDto): Response<Unit>

    @GET("api/tasks/residents")
    suspend fun getAllResidents(): Response<List<ResidentDto>>

    @DELETE("api/tasks/residents/{userId}/{homeId}")
    suspend fun deleteResident(@Path("userId") userId: Int, @Path("homeId") homeId: Int): Response<Unit>

    // ZipCode endpoints
    @GET("api/tasks/zipcodes")
    suspend fun getAllZipCodes(): Response<ApiResponse<List<ZipCodeDto>>>

    @POST("notifications/send")
    suspend fun sendNotification(@Body notification: Map<String, Any>): Response<ApiResponse<Unit>>
}