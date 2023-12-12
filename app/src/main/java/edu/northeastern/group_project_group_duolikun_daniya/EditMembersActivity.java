package edu.northeastern.group_project_group_duolikun_daniya;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class EditMembersActivity extends AppCompatActivity {

    private LinearLayout memberInputContainer;
    private TextInputEditText firstMemberEditText;
    private TextInputEditText secondMemberEditText;
    private Button nextBtn;
    private Button cancelBtn;
    private int membersCount = 2;
    private ArrayList<String> memberNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LogCat - EditMembersActivity", "onCreate() called");
        setContentView(R.layout.activity_edit_members);

        // Initializations
        memberInputContainer = findViewById(R.id.edit_member_linear_layout_container);
        firstMemberEditText = findViewById(R.id.member_input_1);
        secondMemberEditText = findViewById(R.id.member_input_2);
        nextBtn = findViewById(R.id.next_member_btn);
        cancelBtn = findViewById(R.id.cancel_member_btn);
        memberNames = new ArrayList<>();
        String groupID = getIntent().getStringExtra("groupID");
        Log.d("LogCat - EditMembersActivity", "groupID retrieved: " + groupID);


        // Add a new member EditText when the last EditText gets focused
        secondMemberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d("LogCat - EditMembersActivity", "onFocusChange() called");
                if (b) {
                    addNewMemberTextInputEditText();
                    secondMemberEditText.setOnFocusChangeListener(null);
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LogCat - EditMembersActivity", "nextBtn onClick() called");
                retrieveAllMemberNames();
                // todo
            }
        });
    }

    private void retrieveAllMemberNames() {
        memberNames.clear();

        for (int i = 0; i < memberInputContainer.getChildCount(); i++) {
            View child = memberInputContainer.getChildAt(i);

            if (child instanceof TextInputLayout) {
                TextInputEditText editText = (TextInputEditText) ((TextInputLayout) child).getEditText();
                if (editText != null) {
                    String inputText = editText.getText().toString().trim();
                    if (!inputText.isEmpty()) {
                        memberNames.add(inputText);
                    }
                }
            }
        }

        for (String input : memberNames) {
            Log.d("EditMembersActivity", "Input: " + input);
        }
    }

    /**
     * Add a new member EditText when the last EditText gets focused
     */
    private void addNewMemberTextInputEditText() {
        LayoutInflater inflater = LayoutInflater.from(this);
        TextInputLayout textInputLayout = (TextInputLayout) inflater.inflate(R.layout.member_input_layout, memberInputContainer, false);

        TextInputEditText newMemberTextInputEditText = (TextInputEditText) textInputLayout.getEditText();
        if (newMemberTextInputEditText != null) {
            String hint = "Member " + (++membersCount);
            textInputLayout.setHint(hint);
            newMemberTextInputEditText.setId(View.generateViewId());
        }

        memberInputContainer.addView(textInputLayout);

        // Set focus listener to the new EditText
        addFocusListenerToLastEditText();
    }

    /**
     * Add a focus listener to the last EditText in the memberInputContainer
     */
    private void addFocusListenerToLastEditText() {
        int lastIndex = memberInputContainer.getChildCount() - 1;
        TextInputLayout lastTextInputLayout = (TextInputLayout) memberInputContainer.getChildAt(lastIndex);
        TextInputEditText lastEditText = (TextInputEditText) lastTextInputLayout.getEditText();

        if (lastEditText != null) {
            lastEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && v.getId() == lastEditText.getId()) {
                        addNewMemberTextInputEditText();
                        // Remove the focus listener from the last EditText
                        lastEditText.setOnFocusChangeListener(null);
                    }
                }
            });
        }
    }

    /**
     * Hide the keyboard when user clicks outside of the EditText
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof TextInputEditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
