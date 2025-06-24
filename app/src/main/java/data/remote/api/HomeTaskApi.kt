package pt.ipca.hometask.data.remote.api

import pt.ipca.hometask.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface HomeTaskApi {

    // Home endpoints
    @POST("tasks/homes")
    suspend fun createHome(@Body home: HomeDto): Response<HomeDto>

    @GET("tasks/homes")
    suspend fun getAllHomes(): Response<List<HomeDto>>

    @GET("tasks/homes/{id}")
    suspend fun getHomeById(@Path("id") id: Int): Response<HomeDto>

    @GET("tasks/homes/user/{id}/all")
    suspend fun getHomeByUserId(@Path("id") id: Int): Response<ApiResponse<List<HomeDto>>>

    @PUT("tasks/homes/{id}")
    suspend fun updateHome(@Path("id") id: Int, @Body home: HomeDto): Response<HomeDto>

    @DELETE("tasks/homes/{id}")
    suspend fun deleteHome(@Path("id") id: Int): Response<Unit>

    // Task endpoints
    @POST("tasks/tasks")
    suspend fun createTask(@Body task: TaskDto): Response<ApiResponse<TaskDto>>

    @GET("tasks/tasks")
    suspend fun getAllTasks(): Response<List<TaskDto>>

    @GET("tasks/tasks/{id}")
    suspend fun getTaskById(@Path("id") id: Int): Response<TaskDto>

    @GET("tasks/tasks/home/{homeId}")
    suspend fun getTasksByHome(@Path("homeId") homeId: Int): Response<ApiResponse<List<TaskDto>>>

    @GET("tasks/tasks/user/{userId}")
    suspend fun getTasksByUser(@Path("userId") userId: Int): Response<ApiResponse<List<TaskDto>>>

    @PUT("tasks/tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: TaskDto): Response<TaskDto>

    @DELETE("tasks/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Unit>

    // Task Category endpoints
    @POST("tasks/task-categories")
    suspend fun createTaskCategory(@Body category: TaskCategoryDto): Response<TaskCategoryDto>

    @GET("tasks/task-categories")
    suspend fun getAllTaskCategories(): Response<List<TaskCategoryDto>>

    @GET("tasks/task-categories/{id}")
    suspend fun getTaskCategoryById(@Path("id") id: Int): Response<TaskCategoryDto>

    @PUT("tasks/task-categories/{id}")
    suspend fun updateTaskCategory(@Path("id") id: Int, @Body category: TaskCategoryDto): Response<TaskCategoryDto>

    @DELETE("tasks/task-categories/{id}")
    suspend fun deleteTaskCategory(@Path("id") id: Int): Response<Unit>

    // Task Participants endpoints
    @POST("tasks/task-participants")
    suspend fun createTaskParticipant(@Body participant: TaskParticipantDto): Response<Unit>

    @GET("tasks/task-participants/user/{userId}")
    suspend fun getTaskParticipantsByUserId(@Path("userId") userId: Int): Response<List<TaskDto>>

    @GET("tasks/task-participants/task/{taskId}")
    suspend fun getTaskParticipantsByTaskId(@Path("taskId") taskId: Int): Response<List<UserDto>>

    @DELETE("tasks/task-participants/{userId}/{taskId}")
    suspend fun deleteTaskParticipant(@Path("userId") userId: Int, @Path("taskId") taskId: Int): Response<Unit>

    // Residents endpoints
    @POST("tasks/residents")
    suspend fun createResident(@Body resident: ResidentDto): Response<Unit>

    @GET("tasks/residents")
    suspend fun getAllResidents(): Response<List<ResidentDto>>

    @GET("tasks/residents/home/{homeId}")
    suspend fun getResidentsByHomeId(@Path("homeId") homeId: Int): Response<ApiResponse<List<ResidentDto>>>

    @DELETE("tasks/residents/{userId}/{homeId}")
    suspend fun deleteResident(@Path("userId") userId: Int, @Path("homeId") homeId: Int): Response<Unit>

    // ZipCode endpoints
    @GET("tasks/zipcodes")
    suspend fun getAllZipCodes(): Response<ApiResponse<List<ZipCodeDto>>>

    @POST("notifications/send")
    suspend fun sendNotification(@Body notification: Map<String, Any>): Response<ApiResponse<Unit>>
}