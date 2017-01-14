package com.parq.parq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
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
import com.parq.parq.connection.LoginAPI;
import com.parq.parq.connection.ParQURLConstructor;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, APIResponse {
    private EditText usernameLabel;
    private EditText passwordLabel;
    private Button loginButton;
    private Button registerButton;

    private LoginAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setViews();

        showTypeUrlDialog();

        setApp();
        api = new LoginAPI(getApplicationContext(), this);
    }

    private void setViews() {
        usernameLabel = (EditText) findViewById(R.id.username_label);
        passwordLabel = (EditText) findViewById(R.id.password_label);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        usernameLabel.addTextChangedListener(loginTextWatcher);
        passwordLabel.addTextChangedListener(loginTextWatcher);
    }

    private void setApp(){
        SharedPreferences sharedPref = this.getSharedPreferences(
                this.getString(R.string.sharedpref_file_key),
                Context.MODE_PRIVATE);
        String authority = sharedPref.getString(this.getString(R.string.sharedpref_url_slug), null);
        ParQURLConstructor url = new ParQURLConstructor(authority, getApplicationContext());
        App.setSharedPref(sharedPref);
        App.setUrl(url);
    }
/*
    public void loginSuccess(String token) {
        App.setToken(token);
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void loginFailure() {
        Toast.makeText(this, "Bad login or password", Toast.LENGTH_LONG).show();
    }

    public void connectionError(int errorCode) {
        switch (errorCode){
            case App.PARSE_ERROR:
                Toast.makeText(this, "Parse error", Toast.LENGTH_LONG).show();
                break;
            case App.CONNECTION_ERROR:
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                break;
            case App.UNAUTHENTICATED:
                Toast.makeText(this, "Only drivers can login", Toast.LENGTH_SHORT).show();
                break;
        }
    }
*/
    private TextWatcher loginTextWatcher = new TextWatcher() {
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

        if (username.isEmpty() || password.isEmpty())
            loginButton.setEnabled(false);
        else
            loginButton.setEnabled(true);
    }

    private void showTypeUrlDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TypeUrlDialog dialog = new TypeUrlDialog();
        dialog.show(fm, "fragment_type_url");
    }

    @Override
    public void onClick(View view) {
        if(view == loginButton) {
            sendLoginRequest();
        } else if(view == registerButton) {
            startRegisterActivity();
        }
    }

    public void sendLoginRequest() {
        String username = usernameLabel.getText().toString();
        String password = passwordLabel.getText().toString();
        api.login(username, password);
    }

    public void startRegisterActivity(){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    public void responseSuccess(AbstractAPI api) {
        if(this.api == api) {
            App.setToken(this.api.getToken());
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void responseError(AbstractAPI api) {
        if(this.api == api){
            switch(this.api.getResponseCode()){
                case App.HTTP_400:
                    Toast.makeText(this, "Bad login or password", Toast.LENGTH_LONG).show();
                    break;
                case App.HTTP_403:
                    Toast.makeText(this, "Only drivers can login", Toast.LENGTH_SHORT).show();
                    break;
                case App.PARSE_ERROR:
                    Toast.makeText(this, "Parse error", Toast.LENGTH_LONG).show();
                    break;
                case App.CONNECTION_ERROR:
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
