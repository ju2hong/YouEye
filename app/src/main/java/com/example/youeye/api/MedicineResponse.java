package com.example.youeye.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MedicineResponse {
    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response {
        @SerializedName("body")
        private Body body;

        public Body getBody() {
            return body;
        }

        public void setBody(Body body) {
            this.body = body;
        }
    }

    public static class Body {
        @SerializedName("items")
        private List<Medicine> items;

        public List<Medicine> getItems() {
            return items;
        }

        public void setItems(List<Medicine> items) {
            this.items = items;
        }
    }

    public static class Medicine {
        @SerializedName("PRDLST_NM")
        private String name;

        @SerializedName("BSSH_NM")
        private String company;

        @SerializedName("VLD_PRD_YMD")
        private String validity;

        @SerializedName("STRG_MTH_CONT")
        private String storage;

        @SerializedName("ITEM_IMAGE")
        private String imageUrl;

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
}
