package com.company.JitHub.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.company.JitHub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventarioActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int position = 0;
    String documentoAtual;

    LinearLayout ll = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        InstanciaLl();
        ListaNivel1();
    }

    private void ListaNivel1() {
        position = 0;

        final LinearLayout lm = findViewById(R.id.linearMain);
        lm.removeView(ll);
        ll.removeAllViews();

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
                                            lm.removeView(ll);
                                            ll.removeAllViews();
                                            position = 2;
                                            ListaNivel2(document.getId());
                                        }
                                    });
                                } else {
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ll.removeAllViews();
                                            position = 2;
                                            getPerguntas("Nivel1", document.getId());
                                        }
                                    });
                                }

                                ll.addView(btn);

                            }

                            lm.addView(ll);
                        } else {
                            Log.d("tagerr", "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    private void ListaNivel2(final String documento){
        position = 2;

        documentoAtual = documento;

        final LinearLayout lm = findViewById(R.id.linearMain);
        lm.removeView(ll);
        ll.removeAllViews();

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
                                            ll.removeAllViews();
                                            position = 3;
                                            getPerguntas("Nivel1/" + documento + "/Nivel2", document.getId());
                                        }
                                    });
                                }

                                ll.addView(btn);

                            }

                            lm.addView(ll);
                        } else {
                            Log.d("tagerr", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getPerguntas(String collection,String document){

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        final LinearLayout lm = findViewById(R.id.linearMain);
        lm.removeView(ll);
        ll.removeAllViews();

        db.collection(collection)
                .document(document)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> data = document.getData();

                            for (Map.Entry<String, Object> d:
                                    data.entrySet()) {

                                Object nome = d.getKey();

                                if (nome != null) {
                                    ll.setOrientation(LinearLayout.VERTICAL);

                                    TextView txt = new TextView(InventarioActivity.this);
                                    txt.setLayoutParams(params);
                                    txt.setText(nome.toString());
                                    txt.setVisibility(View.VISIBLE);

                                    EditText editText = new EditText(InventarioActivity.this);
                                    editText.setLayoutParams(params);
                                    editText.setVisibility(View.VISIBLE);

                                    ll.addView(txt);
                                    ll.addView(editText);

                                }
                            }
                            Button button = new Button(InventarioActivity.this);
                            button.setLayoutParams(params);
                            button.setText("Enviar");
                            button.setVisibility(View.VISIBLE);
                            ll.addView(button);

                            lm.addView(ll);

                        } else {
                            Log.d("tagerr", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {

        if (position == 0){
            Intent myIntent = new Intent(InventarioActivity.this, PrincipalActivity.class);
            InventarioActivity.this.startActivity(myIntent);
            finish();
        } else if (position == 2) {
            ListaNivel1();
        } else if (position == 3) {
            ListaNivel2(documentoAtual);
        }
    }

    private void InstanciaLl(){
        ll = new LinearLayout(InventarioActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);
    }
}