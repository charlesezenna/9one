package com.fgtit.network;

import com.google.android.gms.tasks.Task;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {
    @FormUrlEncoded
    @POST("students/users/")
    Observable<Response> enroll(@Field("matric_no") String matric_no,
                                @Field("first_name") String first_name,
                                @Field("last_name") String last_name,
                                @Field("middle_name") String middle_name,
                                @Field("madien_name") String madien_name,
                                @Field("sex") String sex,
                                @Field("phone") String phone,
                                @Field("marrital_status") String marrital_status,
                                @Field("dob") String dob,
                                @Field("email") String email,
                                @Field("address") String address,
                                @Field("home_town") String home_town,
                                @Field("religion") String religion,
                                @Field("lga_id") String lga_id,
                                @Field("class_id") String class_id,
                                @Field("department") String department,
                                @Field("faculty") String faculty,
                                @Field("state") String state,
                                @Field("nok_name") String nok_name,
                                @Field("nok_phone") String nok_phone,
                                @Field("nok_email") String nok_email,
                                @Field("nok_address") String nok_address,
                                @Field("country_code") String country_code,
                                @Field("image") String image,
                                @Field("message") String message,
                                @Field("nevsid") String nevsid
    );

    @FormUrlEncoded
    @POST("sections/depts/")
    Observable<List<DeptsResponse>> getDept(@Field("nevsid") String nevsid
    );


    @FormUrlEncoded
    @POST("locations/getStateLgas/")
    Observable<List<StatesResponse>> getStateLgas(@Field("stateId") String stateid
    );

    @Multipart
    @POST("students/upload/")
    Observable<ImgResponse> uploadImage(@Part MultipartBody.Part file,
                                  @Field("matric_no") String matric_no,
                                  @Field("nevsid") String nevsid,
                                  @Part("description") RequestBody description
    );
}
