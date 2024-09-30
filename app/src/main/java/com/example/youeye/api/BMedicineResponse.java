package com.example.youeye.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BMedicineResponse {

    @SerializedName("list")
    public List<Medicine> medicines;

    public class Medicine {
        @SerializedName("BRCD_NO")
        public String barcodeNumber;

        @SerializedName("PRDT_NM")
        public String productName;

        @SerializedName("PRDLST_REPORT_NO")
        public String productReportNumber;

        @SerializedName("CMPNY_NM")
        public String companyName;

        @SerializedName("LAST_UPDT_DTM")
        public String lastUpdateDate;

        @SerializedName("PRDLST_NM")
        public String productSmallCategory;

        @SerializedName("HRNK_PRDLST_NM")
        public String productMediumCategory;

        @SerializedName("HTRK_PRDLST_NM")
        public String productLargeCategory;
    }
}
