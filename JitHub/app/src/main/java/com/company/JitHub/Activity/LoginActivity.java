package com.company.JitHub.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.company.JitHub.DAO.PopulateDatabase;
import com.company.JitHub.Model.Usuario;
import com.company.JitHub.Network.Network;
import com.company.JitHub.R;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    private AutoCompleteTextView mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private Button mLoginSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mLoginView = findViewById(R.id.login);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mLoginView.setError(null);
        mPasswordView.setError(null);

        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
        }

        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
        }

        else {
            closeKeyboard();
            mLoginSignInButton.setVisibility(View.GONE);
            showProgress(true);
            mAuthTask = new UserLoginTask(login, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void showProgress(boolean exibir) {
        mProgressView.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mLogin;
        private final String mPassword;

        UserLoginTask(String login, String password) {
            mLogin = login;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean isConnected = new Network().getConnectivity(getApplicationContext());
            Context context= getApplicationContext();
            Usuario usuarioLogando = new Usuario(mLogin, mPassword);

            if (isConnected){

                boolean validadoNaApi;
                validadoNaApi = usuarioLogando.ValidaUsuarioESenhaNaApi(context);

                if (validadoNaApi == true){
                    Usuario usuarioFromDatabase = usuarioLogando.GetUsuarioFromDatabase(context);
                    if (usuarioFromDatabase == null){
                        usuarioLogando.InsereUsuarioNaBaseInterna(context);
                    } else{
                        usuarioLogando.AtualizaSenhaUsuarioNaBaseInterna(context);
                    }
                }

                return validadoNaApi;
            } else {
                return new Usuario(mLogin, mPassword).ValidaUsuarioESenhaNaBaseInterna(context);
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(myIntent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mLoginSignInButton.setVisibility(View.VISIBLE);
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

