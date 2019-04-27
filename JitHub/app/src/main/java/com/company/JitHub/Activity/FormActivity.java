package com.company.JitHub.Activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.company.JitHub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FormActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final TextInputLayout[] ti = {new TextInputLayout(FormActivity.this)};

        final LinearLayout lm = findViewById(R.id.formsMain);

        String collection = getIntent().getStringExtra("collection");
        String document = getIntent().getStringExtra("document");

        db.collection(collection)
                .document(document)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            final DocumentSnapshot document = task.getResult();
                            Map<String, Object> data = document.getData();

                            for (Map.Entry<String, Object> d:
                                    data.entrySet()) {

                                Object nome = d.getKey();

                                if (nome != null) {

                                    TextInputEditText editText = new TextInputEditText(FormActivity.this);
                                    editText.setHint(nome.toString());
                                    editText.setVisibility(View.VISIBLE);

                                    ti[0].addView(editText);
                                    lm.addView(ti[0]);
                                    ti[0] = new TextInputLayout(FormActivity.this);
                                }
                            }

                            Button button = new Button(FormActivity.this);
                            button.setText("Enviar");
                            button.setVisibility(View.VISIBLE);


                            lm.addView(button);
                        } else {
                            Log.d("tagerr", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

