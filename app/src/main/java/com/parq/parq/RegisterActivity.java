package com.parq.parq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.RegisterAPI;
import com.parq.parq.models.Profile;

public class RegisterActivity extends AppCompatActivity implements APIResponse {
    private EditText usernameLabel;
    private EditText emailLabel;
    private EditText passwordLabel;
    private Button registerButton;

    private RegisterAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setViews();

        api = new RegisterAPI(getApplicationContext(), this);
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
        Profile profile = new Profile();

        profile.setUsername(usernameLabel.getText().toString());
        profile.setEmail(emailLabel.getText().toString());
        profile.setPassword(passwordLabel.getText().toString());

        api.postDriver(profile);
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

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(abstractAPI == this.api) {
            Toast.makeText(this, "Użytkownik dodany", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        if(abstractAPI == this.api) {
            switch (abstractAPI.getResponseCode()){
                case App.HTTP_401:
                    Toast.makeText(this, "Brak uprawnień", Toast.LENGTH_LONG).show();
                    break;
                case App.PARSE_ERROR:
                    Toast.makeText(this, "Błąd parsowania", Toast.LENGTH_LONG).show();
                    break;
                case App.HTTP_400:
                    Toast.makeText(this, "Taka nazwa już istnieje", Toast.LENGTH_LONG).show();
                    break;
                case App.CONNECTION_ERROR:
                default:
                    Toast.makeText(this, "Błąd połączenia", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
