package com.parq.parq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.GetProfileAPI;
import com.parq.parq.connection.LoginAPI;
import com.parq.parq.connection.ParQURLConstructor;
import com.parq.parq.models.Profile;

import net.danlew.android.joda.JodaTimeAndroid;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, APIResponse {
    private EditText usernameLabel;
    private EditText passwordLabel;
    private Button loginButton;
    private Button registerButton;

    private LoginAPI loginAPI;
    private GetProfileAPI profileAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //startActivity(new Intent(this, MainActivity.class));
        setViews();

        showTypeUrlDialog();
        JodaTimeAndroid.init(this);
        setApp();

        loginAPI = new LoginAPI(getApplicationContext(), this);
        profileAPI = new GetProfileAPI(getApplicationContext(), this);
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
        loginAPI.login(username, password);
    }

    public void startRegisterActivity(){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    public void responseSuccess(AbstractAPI api) {
        if(this.loginAPI == api) {
            App.setToken(this.loginAPI.getToken());
            profileAPI.requestProfile();
        } else if(api == this.profileAPI) {
            Profile profile = this.profileAPI.getProfile();
            App.setProfile(profile);
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void responseError(AbstractAPI api) {

        switch(this.loginAPI.getResponseCode()){
            case App.HTTP_400:
                Toast.makeText(this, "Zły login lub hasło", Toast.LENGTH_LONG).show();
                break;
            case App.HTTP_403:
                Toast.makeText(this, "Tylko dla kierowców", Toast.LENGTH_SHORT).show();
                break;
            case App.PARSE_ERROR:
                Toast.makeText(this, "Błąd parsowania", Toast.LENGTH_LONG).show();
                break;
            case App.CONNECTION_ERROR:
            default:
                Toast.makeText(this, "Błąd połączenia", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
