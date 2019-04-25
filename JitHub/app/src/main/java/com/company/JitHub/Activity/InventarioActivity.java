package com.company.JitHub.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;

import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.company.JitHub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class InventarioActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int position = 0;
    String documentoAtual;

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
        position = 0;

        lm.removeAllViews();

        Activity activity = new FragmentActivity();

        db.collection("Nivel1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Button btn = new Button(InventarioActivity.this);
                                btn.setText(document.getId());


                                if (document.getData().isEmpty()) {
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            position = 2;
                                            ListaNivel2(document.getId());
                                        }
                                    });
                                } else {
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            position = 2;
                                            getPerguntas("Nivel1", document.getId());
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

    private void ListaNivel2(final String documento){
        position = 2;
        documentoAtual = documento;
        lm.removeAllViews();

        db.collection("Nivel1/" + documento + "/Nivel2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Button btn = new Button(InventarioActivity.this);
                                btn.setText(document.getId());

                                if (document.getData().isEmpty()){
                                    Log.d("TAG CAIO VAZIO", document.getData().toString(), task.getException());
                                } else {
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            position = 3;
                                            getPerguntas("Nivel1/" + documento + "/Nivel2", document.getId());
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

    private void getPerguntas(final String collection, String document){

        lm.removeAllViews();

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

                                    TextInputEditText editText = new TextInputEditText(InventarioActivity.this);
                                    editText.setHint(nome.toString());
                                    editText.setVisibility(View.VISIBLE);

                                    ti.addView(editText);
                                    lm.addView(ti);
                                    ti = new TextInputLayout(InventarioActivity.this);
                                }
                            }

                            Button button = new Button(InventarioActivity.this);
                            button.setText("Enviar");
                            button.setVisibility(View.VISIBLE);

                            button.setOnClickListener(new View.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onClick(View v) {

                                    Map<String, Object> resposta = new HashMap<>();
                                    resposta.put("reference",document.getReference());
                                    int childCount = lm.getChildCount();
                                    for (int i = 0; i < childCount; i++){
                                        View view = lm.getChildAt(i);
                                        if (view instanceof TextInputLayout){

                                            int childCount1 = ((TextInputLayout) view).getChildCount();
                                            for (int o = 0; o<childCount1; o++){
                                                View view2 = ((TextInputLayout) view).getChildAt(o);
                                                if (view2 instanceof FrameLayout){

                                                    int childCount2 = ((FrameLayout) view2).getChildCount();
                                                    for (int p = 0; p<childCount2; p++){
                                                        View childAt = ((FrameLayout) view2).getChildAt(p);
                                                        if (childAt instanceof EditText){

                                                            String s1 = ((TextInputLayout) view).getHint().toString();
                                                            String s = ((EditText) childAt).getText().toString();

                                                            resposta.put(s1, s);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                                    LocalDateTime now = LocalDateTime.now();

                                    db.collection("Respostas")
                                            .document(dtf.format(now))
                                            .set(resposta)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(InventarioActivity.this, "Formulário salvo com sucesso!", Toast.LENGTH_SHORT).show();
                                                    Log.d("Add", "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(InventarioActivity.this, "Problema ao salvar formulário!", Toast.LENGTH_SHORT).show();
                                                    Log.w("Err", "Error writing document", e);
                                                }
                                            });

                                    ListaNivel1();
                                }
                            });
                            lm.addView(button);
                        } else {
                            Log.d("tagerr", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {

        if (position == 0){
            Intent myIntent = new Intent(InventarioActivity.this, PrincipalActivity.class);
            InventarioActivity.this.startActivity(myIntent);
            finish();
        } else if (position == 2) {
            int childCount = lm.getChildCount();
            for (int i = 0; i < childCount; i++){
                View childAt = lm.getChildAt(i);
//                Log.d("tagerr", childAt.toString());
                if (childAt instanceof android.support.v7.widget.Toolbar){
                    continue;
                } else if (childAt instanceof TextInputLayout) {
                    ((TextInputLayout) childAt).removeAllViews();
                }else {
                    lm.removeView(childAt);
                }
            }
            ListaNivel1();
        } else if (position == 3) {
            int childCount = lm.getChildCount();
            for (int i = 0; i < childCount; i++){
                View childAt = lm.getChildAt(i);
                if (!(childAt instanceof Toolbar)){
                    lm.removeView(childAt);
                }
            }
            ListaNivel2(documentoAtual);
        }
    }

    private void InstanciaLl(){
        ti = new TextInputLayout(InventarioActivity.this);
        lm = findViewById(R.id.linearMain);
        lm.setPadding(16,16,16,16);
    }
}