package com.parq.parq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameLabel;
    private EditText emailLabel;
    private EditText passwordLabel;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setViews();
    }

    private void setViews() {
        usernameLabel = (EditText) findViewById(R.id.username_label);
        emailLabel = (EditText) findViewById(R.id.email_label);
        passwordLabel = (EditText) findViewById(R.id.password_label);
        registerButton = (Button) findViewById(R.id.register_button);

        usernameLabel.addTextChangedListener(registerTextWatcher);
        emailLabel.addTextChangedListener(registerTextWatcher);
        passwordLabel.addTextChangedListener(registerTextWatcher);
    }

    public void registerOnClick(View view) {
        //TODO api call
        registerPostSuccess();
    }

    public void registerPostSuccess(){
        finish();
    }

    public void connectionError(int errorCode) {
        switch (errorCode){
            case App.UNAUTHENTICATED:
                Toast.makeText(this, "Unauthenticated", Toast.LENGTH_LONG).show();
                break;
            case App.CONNECTION_ERROR:
                Toast.makeText(this, "Connection error", Toast.LENGTH_LONG).show();
                break;
            case App.PARSE_ERROR:
                Toast.makeText(this, "Parse error", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkLabelsForEmptyValues();
        }
    };

    private void checkLabelsForEmptyValues() {
        String username = usernameLabel.getText().toString();
        String password = passwordLabel.getText().toString();
        String email = emailLabel.getText().toString();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty())
            registerButton.setEnabled(false);
        else
            registerButton.setEnabled(true);
    }
}
