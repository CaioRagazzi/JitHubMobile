package com.company.JitHub.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FormActivity extends AppCompatActivity {


    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputLayout ti;
    LinearLayout lm;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        InstanciaLayout();

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
                                Object value =  d.getValue();

                                if (value != null) {

                                    ListaPergunta(value.toString());

                                }
                            }

                            Button button = new Button(FormActivity.this);
                            button.setText("Enviar");
                            button.setVisibility(View.VISIBLE);

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Map<String, Object> resposta = new HashMap<>();
                                    resposta.put("reference",document.getReference());
                                    int childCount = lm.getChildCount();

                                    for (int p = 0; p<childCount; p++) {
                                        View view = lm.getChildAt(p);
                                        if (view instanceof TextInputLayout) {

                                            EditText editText = ((TextInputLayout) view).getEditText();


                                            String s1 = ((TextInputLayout) view).getHint().toString();
                                            String s = editText.getText().toString();;

                                            resposta.put(s1, s);
                                        }
                                    }

                                    DateFormat dtf = new SimpleDateFormat("yyyyMMddHHmmss");
                                    Date now = new Date();

                                    db.collection("Respostas")
                                            .document(dtf.format(now))
                                            .set(resposta)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(FormActivity.this, "Formulário salvo com sucesso!", Toast.LENGTH_SHORT).show();
                                                    Log.d("Add", "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(FormActivity.this, "Problema ao salvar formulário!", Toast.LENGTH_SHORT).show();
                                                    Log.w("Err", "Error writing document", e);
                                                }
                                            });

                                    Intent myIntent = new Intent(FormActivity.this, PrincipalActivity.class);
                                    FormActivity.this.startActivity(myIntent);
                                }
                            });

                            lm.addView(button, 0);
                        } else {
                            Log.d("tagerr", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void InstanciaLayout() {
        ti = new TextInputLayout(FormActivity.this);
        lm = findViewById(R.id.formsMain);
    }

    private void ListaPergunta(String reference) {

        db.document(reference)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        DocumentSnapshot result = task.getResult();

                        Map<String, Object> data = result.getData();

                        Object pergunta = data.get("pergunta");
                        Object tipo = data.get("tipo");

                        switch (tipo.toString()){
                            case "text":
                                TextInputEditText editText = new TextInputEditText(FormActivity.this);
                                editText.setHint(pergunta.toString());
                                editText.setVisibility(View.VISIBLE);

                                ti.addView(editText);
                                lm.addView(ti, 0);
                                ti = new TextInputLayout(FormActivity.this);
                                break;
                            case "number":
                                TextInputEditText numberEditText = new TextInputEditText(FormActivity.this);
                                numberEditText.setHint(pergunta.toString());
                                numberEditText.setVisibility(View.VISIBLE);
                                numberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                numberEditText.setLongClickable(false);

                                ti.addView(numberEditText);
                                lm.addView(ti, 0);
                                ti = new TextInputLayout(FormActivity.this);
                                break;
                            case "date":
                                final TextInputEditText dateEditText = new TextInputEditText(FormActivity.this);
                                dateEditText.setHint(pergunta.toString());
                                dateEditText.setVisibility(View.VISIBLE);
                                dateEditText.setFocusable(false);
                                dateEditText.setLongClickable(false);

                                final Calendar myCalendar = Calendar.getInstance();

                                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        myCalendar.set(Calendar.YEAR, year);
                                        myCalendar.set(Calendar.MONTH, month);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                        Locale myLocale = new Locale("pt", "BR");
                                        String myFormat = "dd/MM/yy"; //In which you need put here
                                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, myLocale);

                                        dateEditText.setText(sdf.format(myCalendar.getTime()));
                                    }
                                };

                                dateEditText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new DatePickerDialog(FormActivity.this, date, myCalendar
                                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                    }
                                });

                                ti.addView(dateEditText);
                                lm.addView(ti, 0);
                                ti = new TextInputLayout(FormActivity.this);
                                break;
                            default:
                                break;
                        }

                    } else {
                        Log.d("tagerr", "Error getting documents: ", task.getException());
                    }
                }
            });
    }
}

