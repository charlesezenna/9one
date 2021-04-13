package com.fgtit.network;

import com.fgtit.data.UserItem;
import com.fgtit.utils.AppPrefs;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ApiHelper {
    //can't get class_id and country_code and message
    public static Observable<Response> getEnrollObservable(UserItem person) {
        return APIClient.getService().enroll(person.matric_no, person.first_name,
                person.last_name, person.middle_name, person.madien_name, person.sex,
                person.phone, person.marrital_status, person.dob, person.email,
                person.address, person.home_town, person.religion, person.lga, person.id, person.depttype,
                person.faculty, person.state, person.nok_name, person.nok_phone, person.nok_email,
                person.nok_address, "", person.photo, "", AppPrefs.get().getNevsid());
    }

    public  static Observable<ImgResponse> getImgObservable(MultipartBody.Part part, String matNo, RequestBody desc){
        return APIClient.getService().uploadImage(part,matNo,AppPrefs.get().getNevsid(),desc);
    }
}
