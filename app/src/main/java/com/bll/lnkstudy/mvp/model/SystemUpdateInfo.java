package com.bll.lnkstudy.mvp.model;

import com.google.gson.annotations.SerializedName;

public class SystemUpdateInfo {

    @SerializedName("NO")
    public String version;
    @SerializedName("Size")
    public Long size;
    @SerializedName("Description")
    public String description;
    @SerializedName("Hash")
    public String md5;
    @SerializedName("Link")
    public String otaUrl;

}
