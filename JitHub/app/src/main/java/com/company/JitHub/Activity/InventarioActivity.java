package com.company.JitHub.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;

import com.company.JitHub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;


public class InventarioActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextInputLayout ti = null;
    LinearLayout lm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        InstanciaLl();
        ListaNivel1();
    }

    private void ListaNivel1() {

        lm.removeAllViews();

        db.collection("Nivel1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("tagerr", task.getResult().toString(), task.getException());

                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Button btn = new Button(InventarioActivity.this);
                                btn.setText(document.getId());

                                Map<String, Object> data = document.getData();
                                String key = data.entrySet().iterator().next().getKey();

//                                if (document.getData().isEmpty()) {
                                if (data.size() == 1 && key.equals("collection")) {

                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent myIntent = new Intent(InventarioActivity.this, PerguntasActivity.class);
                                            myIntent.putExtra("documento", document.getReference().getPath());
                                            myIntent.putExtra("nivel", 2);
                                            InventarioActivity.this.startActivity(myIntent);
                                        }
                                    });

                                } else {

                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent myIntent = new Intent(InventarioActivity.this, FormActivity.class);
                                            myIntent.putExtra("collection", "Nivel1");
                                            myIntent.putExtra("document", document.getId());
                                            myIntent.putExtra("path", document.getReference().getPath());
                                            InventarioActivity.this.startActivity(myIntent);
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

    private void InstanciaLl(){
        ti = new TextInputLayout(InventarioActivity.this);
        lm = findViewById(R.id.linearMain);
        lm.setPadding(16,16,16,16);
    }
}