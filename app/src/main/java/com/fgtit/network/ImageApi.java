package com.fgtit.network;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageApi {

    @POST("/students/upload/")
    Observable<ImgResponse> uploadImage(
            @Header("Content-Type")
                    String headerValue,
            @Body ImageRequest imageRequest
    );
}
