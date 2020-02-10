package com.techcloud.isecurity.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Employee;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.models.Guest;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @Multipart
    @POST("img_proc/")
    Single<JsonObject> uploadImage(@Part MultipartBody.Part image, @Header("api-token") String jwtToken);

    @POST("buildings/")
    Single<JsonObject> createBuilding(@Body Building building);

    @POST("guards/")
    Single<JsonObject> createGuard(@Body Guard guard);

    @POST("companies/")
    Single<JsonObject> createCompany(@Body Company company, @Header("api-token") String jwtToken);

    @POST("guards/login")
    Single<JsonObject> loginGuard(@Body JsonObject object);

    @POST("guards/logout")
    Single<JsonObject> logoutGuard(@Body JsonObject object);

    @POST("guests/")
    Single<JsonObject> createGuest(@Body Guest guest, @Header("api-token") String jwtToken);

    @POST("employees/")
    Single<JsonObject> createEmployee(@Body Employee employee, @Header("api-token") String jwtToken);

    @GET("buildings/")
    Single<JsonArray> getBuildings(@Header("api-token") String jwtToken);

    @GET("buildings/{id}")
    Single<JsonObject> getOneBuilding(@Header("api-token") String jwtToken, @Path("id") int building_id);

    @GET("guards/")
    Single<JsonArray> getGuards(@Header("api-token") String jwtToken);

    @GET("companies/")
    Single<JsonArray> getCompanies(@Header("api-token") String jwtToken);

    @GET("guests/")
    Single<JsonArray> getGuests(@Header("api-token") String jwtToken);

    @GET("employees/")
    Single<JsonArray> getEmployees(@Header("api-token") String jwtToken);

    @PUT("buildings/{id}")
    Single<JsonObject> updateBuilding(@Path("id") int building_id, @Body Building building, @Header("api-token") String jwtToken);

    @PUT("guards/{id}")
    Single<JsonObject> updateGuard(@Path("id") int guard_id, @Body Guard guard, @Header("api-token") String jwtToken);

    @PUT("companies/{id}")
    Single<JsonObject> updateCompany(@Path("id") int company_id, @Body Company company, @Header("api-token") String jwtToken);

    @PUT("guests/{id}")
    Single<JsonObject> updateGuest(@Path("id") int guest_id, @Body Guest guest, @Header("api-token") String jwtToken);

    @PUT("employees/{id}")
    Single<JsonObject> updateEmployee(@Path("id") int emp_id, @Body Employee employee, @Header("api-token") String jwtToken);

    @DELETE("buildings/{id}")
    Completable deleteBuilding(@Path("id") int building_id, @Header("api-token") String jwtToken);

    @DELETE("guards/{id}")
    Completable deleteGuard(@Path("id") int guard_id, @Header("api-token") String jwtToken);

    @DELETE("companies/{id}")
    Completable deleteCompany(@Path("id") int company_id, @Header("api-token") String jwtToken);

    @DELETE("guests/{id}")
    Completable deleteGuest(@Path("id") int guest_id, @Header("api-token") String jwtToken);

    @DELETE("employees/{id}")
    Completable deleteEmployee(@Path("id") int emp_id, @Header("api-token") String jwtToken);

}
