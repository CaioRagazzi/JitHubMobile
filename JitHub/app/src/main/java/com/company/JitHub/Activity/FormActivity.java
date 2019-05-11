package com.company.JitHub.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.company.JitHub.Adapter.ImageAdapter;
import com.company.JitHub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FormActivity extends AppCompatActivity {


    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    static final int REQUEST_TAKE_PHOTO = 1;
    TextInputLayout ti;
    LinearLayout lm;
    GridView grid;
    private String[] mCurrentPhotoPath = new String[10];
    List<File> files = new ArrayList<>();
    Integer contador = 0;
    Integer contImages = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        InstanciaLayout();

        final String collection = getIntent().getStringExtra("collection");
        final String documentReference = getIntent().getStringExtra("document");

        db.collection(collection)
                .document(documentReference)
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
                                            String s = editText.getText().toString();

                                            resposta.put(s1, s);
                                        }
                                    }

                                    DateFormat dtf = new SimpleDateFormat("yyyyMMddHHmmss");
                                    Date now = new Date();
                                    String dateNow = dtf.format(now);

                                    //QR CODE!!!!
                                    GravarQRCode("Respostas/" + dateNow, dateNow);
                                    try {
                                        GravaImagens(mCurrentPhotoPath, dateNow);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    db.collection("Respostas")
                                            .document(dateNow)
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

    @Override
    public void onResume() {
        super.onResume();
        grid.setAdapter(new ImageAdapter(FormActivity.this, mCurrentPhotoPath));
    }

    private void GravarQRCode(String reference, String fileName) {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(reference, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e){
            e.printStackTrace();
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference qrcodeRef = storageRef.child(fileName + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = qrcodeRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormActivity.this, "Erro ao fazer upload do QRCode", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    private void GravaImagens(String[] imagens, String folderName) throws FileNotFoundException {

        for (String img: imagens) {
            if (img == null){
                return;
            }
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            StorageReference imgRef = storageRef.child(folderName + "/" + contImages);
            contImages++;

            InputStream stream = new FileInputStream(new File(img));

            UploadTask uploadTask = imgRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FormActivity.this, "DEU PAU!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(FormActivity.this, "MANDOU BEM!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void InstanciaLayout() {
        ti = new TextInputLayout(FormActivity.this);
        lm = findViewById(R.id.formsMain);
        grid = new GridView(FormActivity.this);

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
                                        closeKeyboard();
                                        new DatePickerDialog(FormActivity.this, date, myCalendar
                                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                    }
                                });

                                ti.addView(dateEditText);
                                lm.addView(ti, 0);
                                ti = new TextInputLayout(FormActivity.this);
                                break;
                            case "camera":
                                Button btn = new Button(FormActivity.this);
                                btn.setText("Adicionar foto");
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        // Ensure that there's a camera activity to handle the intent
                                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                            // Create the File where the photo should go
                                            File photoFile = null;
                                            try {
                                                photoFile = createImageFile();
                                            } catch (IOException ex) {
                                                //TODO Adicionar algo aqui!
                                            }
                                            // Continue only if the File was successfully created
                                            if (photoFile != null) {
                                                Uri photoURI = FileProvider.getUriForFile(FormActivity.this, "com.company.JitHub", photoFile);
                                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                                            }
                                        }
                                    }
                                });

                                Display display = FormActivity.this.getWindowManager().getDefaultDisplay();
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                display.getMetrics(displayMetrics);

                                int width = display.getWidth();
                                int height = display.getHeight();

                                grid.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
                                grid.setBackgroundColor(Color.WHITE);
                                grid.setNumColumns(4);
                                grid.setColumnWidth(50);
                                grid.setVerticalSpacing(5);
                                grid.setHorizontalSpacing(5);
                                grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
//                                grid.setGravity();
                                lm.addView(btn, 1);
                                lm.addView(grid);
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

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */

        );

        files.add(image);
        mCurrentPhotoPath[contador] = image.getAbsolutePath();
        contador++;
        return image;
    }
}

