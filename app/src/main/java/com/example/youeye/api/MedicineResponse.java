package com.example.youeye.api;

import com.example.youeye.home.Medicine;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MedicineResponse {
    @SerializedName("body")
    private Body body;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
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
}
