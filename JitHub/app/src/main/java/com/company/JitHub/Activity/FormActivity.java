package com.company.JitHub.Activity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;

import com.company.JitHub.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FormActivity extends BaseActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_form;
    }
}
