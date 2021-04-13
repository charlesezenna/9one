package com.fgtit.network;

import com.google.gson.annotations.SerializedName;

public class StatesResponse {
    public String lga;

    @SerializedName("lid")
    public String lgaId;
    public String state;

    @SerializedName("state_id")
    public String stateId;
}
