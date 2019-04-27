package com.company.JitHub.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.company.JitHub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PerguntasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perguntas);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final LinearLayout lm = findViewById(R.id.perguntasMain);

        final String documento = getIntent().getStringExtra("documento");
        final int nivel = getIntent().getIntExtra("nivel", 0);

        final String collection = documento.concat("/Nivel" + nivel);

        db.collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Button btn = new Button(PerguntasActivity.this);
                                btn.setText(document.getId());

                                if (document.getData().isEmpty()){

                                    DocumentReference reference = document.getReference();

                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent myIntent = new Intent(PerguntasActivity.this, PerguntasActivity.class);
                                            myIntent.putExtra("documento", document.getReference().getPath());
                                            myIntent.putExtra("nivel", nivel + 1);
                                            PerguntasActivity.this.startActivity(myIntent);
                                        }
                                    });
                                } else {
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent myIntent = new Intent(PerguntasActivity.this, FormActivity.class);
                                            myIntent.putExtra("collection", collection);
                                            myIntent.putExtra("document", document.getId());
                                            PerguntasActivity.this.startActivity(myIntent);
                                        }
                                    });
                                }

                                lm.addView(btn);

                            }
                        } else {
                            Log.d("tagerr", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
