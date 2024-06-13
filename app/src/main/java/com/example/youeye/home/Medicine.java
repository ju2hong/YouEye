package com.example.youeye.home;

import com.google.gson.annotations.SerializedName;

public class Medicine {
    @SerializedName("PRDLST_NM")
    private String name;

    @SerializedName("BSSH_NM")
    private String company;

    @SerializedName("VLD_PRD_YMD")
    private String validity;

    @SerializedName("STRG_MTH_CONT")
    private String storage;

    @SerializedName("IMG_URL")
    private String imageUrl; // 이미지 URL 필드 추가

    public Medicine() {
    }

    public Medicine(String name, String company, String validity, String storage, String imageUrl) {
        this.name = name;
        this.company = company;
        this.validity = validity;
        this.storage = storage;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
