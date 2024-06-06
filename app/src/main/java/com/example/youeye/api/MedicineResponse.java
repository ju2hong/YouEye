package com.example.youeye.api;

import com.example.youeye.home.Medicine; // 여기에 Medicine 클래스를 임포트합니다.
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MedicineResponse {
    @SerializedName("medicines")
    private List<Medicine> medicines;

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
    }
}
