package com.example.youeye.member;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.youeye.R;
import com.example.youeye.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class IDMemberActivity extends AppCompatActivity {

    private List<ImageButton> imageButtons;
    private int currentIndex = 0;
    private StringBuilder inputNumber; // 사용자가 입력한 숫자를 저장할 StringBuilder

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idmember);

        inputNumber = new StringBuilder(); // StringBuilder 초기화

        // ImageButton을 리스트로 초기화합니다.
        imageButtons = new ArrayList<>();
        imageButtons.add(findViewById(R.id.textButton1));
        imageButtons.add(findViewById(R.id.textButton2));
        imageButtons.add(findViewById(R.id.textButton3));
        imageButtons.add(findViewById(R.id.textButton4));

        // 모든 ImageButton을 초기 상태로 설정합니다.
        for (ImageButton imageButton : imageButtons) {
            imageButton.setImageResource(R.drawable.logintext);
            imageButton.setTag(null);
        }

        // onCreate 메서드 안에 0부터 9까지의 숫자에 대한 클릭 리스너를 추가합니다.
        for (int i = 0; i < 10; i++) {
            final int number = i; // 클로저를 위해 final 변수로 설정합니다.
            int buttonId = getResources().getIdentifier("key" + i, "id", getPackageName());
            findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton currentImageButton = imageButtons.get(currentIndex);
                    currentImageButton.setImageResource(R.drawable.textinput);
                    currentImageButton.setTag(String.valueOf(number)); // 숫자를 문자열로 변환하여 태그에 설정합니다.
                    inputNumber.append(number); // 입력된 숫자를 추가합니다.
                    moveToNextImageButton();
                }
            });
        }
        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
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
        // LayoutInflater를 사용하여 custom_dialog_layout.xml 파일을 inflate합니다.
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        // AlertDialog.Builder를 사용하여 다이얼로그를 생성합니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // 다이얼로그 내의 TextView에 제목과 메시지를 설정합니다.
        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        titleTextView.setText("아이디 확인");
        String message = getString(R.string.confirmation_message, enteredNumber);
        messageTextView.setText(message);

        // AlertDialog를 생성하고 표시합니다.
        dialog = builder.create();
        // 배경 설정
        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.custom_dialog_background, null);
        dialog.getWindow().setBackgroundDrawable(background);
        dialog.show();
    }
    // 사용자가 Yes를 선택한 경우, LoginActivity로 화면을 전환하는 메서드
    public void onYesButtonClick(View view) {
        // 로그인 처리를 한 뒤 로그인 액티비티의 아이디 입력 버튼을 변경합니다.
        // 이 코드는 로그인 처리가 완료되었다고 가정하고 작성된 것입니다.
        changeIdButtonInLoginActivity();
        Intent intent = new Intent(IDMemberActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    // LoginActivity에서 아이디 입력 버튼을 변경하는 메서드
    private void changeIdButtonInLoginActivity() {
        // 로그인 액티비티의 아이디 입력 창과 비밀번호 입력 창에 대한 레이아웃 파일을 가져옵니다.
        View loginLayout = LayoutInflater.from(this).inflate(R.layout.activity_member, null);

        // 가져온 레이아웃에서 아이디 입력 버튼을 찾아서 색상을 변경합니다.
        ImageButton idButton = loginLayout.findViewById(R.id.idInputButton); // 예시에서는 idInputButton으로 가정합니다.
        idButton.setImageResource(R.drawable.inputbutton); // 새 이미지로 아이디 입력 버튼을 설정합니다.

        // 변경된 레이아웃을 다시 화면에 적용합니다.
        setContentView(loginLayout);
    }
    // 사용자가 No를 선택한 경우, 입력된 숫자를 다시 초기화하는 메서드
    public void onNoButtonClick(View view) {
        clearAllImageButtons(); // 모든 이미지 버튼을 초기화합니다.
        inputNumber.setLength(0);
        dialog.dismiss(); // 팝업창을 닫습니다
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

