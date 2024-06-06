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

    public Medicine() {
    }

    public Medicine(String name, String company, String validity, String storage) {
        this.name = name;
        this.company = company;
        this.validity = validity;
        this.storage = storage;
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
}
