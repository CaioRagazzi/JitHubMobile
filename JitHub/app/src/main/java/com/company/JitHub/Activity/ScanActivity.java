package com.company.JitHub.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.company.JitHub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputLayout ti;
    LinearLayout lm;
    Map<String, Object> mapData;
    String contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            InstanciaLayout();

            if (resultCode == RESULT_OK) {
                contents = data.getStringExtra("SCAN_RESULT");
                try {
                    db.document(contents)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){

                                        final DocumentSnapshot document = task.getResult();
                                        mapData = document.getData();

                                        DocumentReference reference = (DocumentReference) mapData.get("reference");

                                        String path = reference.getPath();

                                        listaFormulario(path);

                                    } else {
                                        Log.d("tagerr", "Error getting documents: ", task.getException());
                                        Toast.makeText(ScanActivity.this, "QRCode não existe na base de dados!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(this, "QRCode não existe na base de dados!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, PrincipalActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            if(resultCode == RESULT_CANCELED){
                Intent intent = new Intent(this, PrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void listaFormulario(String documentReference){

        db.document(documentReference)
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

                                if (d.getKey().equals("collection")){
                                    continue;
                                }

                                Object value =  d.getValue();

                                if (value != null) {

                                    ListaPergunta(value.toString());

                                }
                            }

                            Button button = new Button(ScanActivity.this);
                            button.setText("Atualizar");
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
                                            String s = editText.getText().toString();

                                            resposta.put(s1, s);
                                        }
                                    }

                                    db.document(contents)
                                            .set(resposta)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ScanActivity.this, "Formulário atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                                    Log.d("Add", "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ScanActivity.this, "Problema ao salvar formulário!", Toast.LENGTH_SHORT).show();
                                                    Log.w("Err", "Error writing document", e);
                                                }
                                            });

                                    Intent myIntent = new Intent(ScanActivity.this, PrincipalActivity.class);
                                    ScanActivity.this.startActivity(myIntent);
                                }
                            });

                            lm.addView(button, 0);
                        } else {
                            Log.d("tagerr", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void ListaPergunta(String reference) {
        Log.d("tagerr", reference);
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
                                TextInputEditText editText = new TextInputEditText(ScanActivity.this);
                                editText.setHint(pergunta.toString());
                                editText.setVisibility(View.VISIBLE);
                                editText.setText(mapData.get(pergunta.toString()).toString());

                                ti.addView(editText);
                                lm.addView(ti, 0);
                                ti = new TextInputLayout(ScanActivity.this);
                                break;
                            case "number":
                                TextInputEditText numberEditText = new TextInputEditText(ScanActivity.this);
                                numberEditText.setHint(pergunta.toString());
                                numberEditText.setVisibility(View.VISIBLE);
                                numberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                numberEditText.setLongClickable(false);
                                numberEditText.setText(mapData.get(pergunta.toString()).toString());

                                ti.addView(numberEditText);
                                lm.addView(ti, 0);
                                ti = new TextInputLayout(ScanActivity.this);
                                break;
                            case "date":
                                final TextInputEditText dateEditText = new TextInputEditText(ScanActivity.this);
                                dateEditText.setHint(pergunta.toString());
                                dateEditText.setVisibility(View.VISIBLE);
                                dateEditText.setFocusable(false);
                                dateEditText.setLongClickable(false);
                                dateEditText.setText(mapData.get(pergunta.toString()).toString());

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
                                        closeKeyboard();
                                        new DatePickerDialog(ScanActivity.this, date, myCalendar
                                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                    }
                                });

                                ti.addView(dateEditText);
                                lm.addView(ti, 0);
                                ti = new TextInputLayout(ScanActivity.this);
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

    private void InstanciaLayout() {
        ti = new TextInputLayout(ScanActivity.this);
        lm = findViewById(R.id.formsScanMain);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
