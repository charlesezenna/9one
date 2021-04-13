package com.fgtit.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageRequest {


    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("matricNo")
    @Expose
    private String matric_no;

    @SerializedName("nevsId")
    @Expose
    private String nevsid;

    public String getMatric_no() {
        return matric_no;
    }

    public void setMatric_no(String matric_no) {
        this.matric_no = matric_no;
    }

    public String getNevsid() {
        return nevsid;
    }

    public void setNevsid(String nevsid) {
        this.nevsid = nevsid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
