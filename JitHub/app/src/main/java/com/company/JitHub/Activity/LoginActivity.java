package com.company.JitHub.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.company.JitHub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

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

        mLoginSignInButton = findViewById(R.id.email_sign_in_button);
        mLoginSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {

        mLoginView.setError(null);
        mPasswordView.setError(null);

        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(login)){
            if (TextUtils.isEmpty(login)) {
                mLoginView.setError(getString(R.string.error_field_required));
                mPasswordView.requestFocus();
            }
            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                mPasswordView.requestFocus();
            }
        }

        else {
            closeKeyboard();
            showLoginButton(false);
            showProgress(true);
            UserLoginTask(login, password);
        }
    }

    private void UserLoginTask(String login, final String password){

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("Usuarios").document(login);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.get("senha").toString().equals(password)){
                                Intent myIntent = new Intent(LoginActivity.this, PrincipalActivity.class);
                                LoginActivity.this.startActivity(myIntent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Senha inválida!", Toast.LENGTH_SHORT).show();
                                showProgress(false);
                                showLoginButton(true);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login não encontrado!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            showLoginButton(true);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Erro ao efetuar login!", Toast.LENGTH_SHORT).show();
                        Log.d("LOGGGGEERRRR", "FALHA", task.getException());
                        showProgress(false);
                        showLoginButton(true);
                    }
                }
            });
    }


    private void showProgress(boolean exibir) {
        mProgressView.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    private void showLoginButton(boolean exibir) {
        mLoginSignInButton.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

