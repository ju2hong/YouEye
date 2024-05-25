package com.example.youeye;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PWLoginActivity extends AppCompatActivity {
    // 이미지 버튼 리스트를 선언하고 초기화합니다.
    private List<ImageButton> imageButtons;
    // 현재 포커싱된 버튼의 인덱스를 나타내는 변수를 선언하고 초기화합니다.
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwlongin);

        // ImageButton 리스트 초기화
        imageButtons = new ArrayList<>();
        imageButtons.add(findViewById(R.id.textPwButton1));
        imageButtons.add(findViewById(R.id.textPwButton2));
        imageButtons.add(findViewById(R.id.textPwButton3));
        imageButtons.add(findViewById(R.id.textPwButton4));

        // 모든 ImageButton을 초기 상태로 설정합니다.
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }

        // onCreate 메서드 안에 0부터 9까지의 숫자에 대한 클릭 리스너를 추가합니다.
        for (int i = 0; i < 10; i++) {
            final int number = i; // 클로저를 위해 final 변수로 설정합니다.
            int buttonId = getResources().getIdentifier("pwkey" + i, "id", getPackageName());
            findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton currentImageButton = imageButtons.get(currentIndex);
                    currentImageButton.setImageResource(R.drawable.textinput);
                    currentImageButton.setTag(String.valueOf(number)); // 숫자를 문자열로 변환하여 태그에 설정합니다.
                    moveToNextImageButton();
                }
            });
        }
        findViewById(R.id.deletePwButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLastCharacter();
            }
        });
    }
    // 삭제 버튼 클릭 이벤트 핸들러
    public void deleteLastCharacter() {
        // 현재 입력된 숫자가 없는 경우 삭제 버튼을 무시합니다.
        if (currentIndex <= 0) {
            return;
        }

        // 직전에 입력된 ImageButton에서 이미지를 초기화하고 태그를 제거합니다.
        int previousIndex = currentIndex - 1;
        ImageButton previousImageButton = imageButtons.get(previousIndex);
        previousImageButton.setImageResource(R.drawable.logintext);
        previousImageButton.setTag(null);

        // 직전 ImageButton으로 포커스를 이동합니다.
        currentIndex = previousIndex;
        imageButtons.get(currentIndex).requestFocus();
    }

    // 다음 ImageButton으로 포커스를 이동하는 메서드
    private void moveToNextImageButton() {
        currentIndex++;
        if (currentIndex >= imageButtons.size()) {
            // 모든 ImageButton에 숫자가 입력되었을 때 확인 팝업창을 띄웁니다.
            showConfirmationDialog();
            currentIndex = 0; // 첫 번째 ImageButton으로 이동합니다.
        } else {
            imageButtons.get(currentIndex).requestFocus();
        }
    }

    // 확인 팝업창을 띄우는 메서드
    private void showConfirmationDialog() {
        StringBuilder enteredNumber = new StringBuilder();
        for (ImageButton imageButton : imageButtons) {
            String number = (String) imageButton.getTag();
            if (number != null) {
                enteredNumber.append(number);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("비밀번호 확인");
        builder.setMessage("입력된 숫자가 맞습니까? " + enteredNumber.toString());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 Yes를 선택한 경우, LoginActivity로 화면을 전환합니다.
                Intent intent = new Intent(PWLoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 No를 선택한 경우, 입력된 숫자를 다시 수정할 수 있도록 처리할 수 있습니다.
                clearAllImageButtons(); // 모든 이미지 버튼을 초기화합니다.
            }
        });
        builder.show();
    }

    // 모든 ImageButton을 초기화하는 메서드
    public void clearAllImageButtons() {
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }
        currentIndex = 0; // 현재 인덱스를 초기화하여 처음부터 다시 입력할 수 있도록 합니다.
    }
}


