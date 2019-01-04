package com.ichiban.kelompok1.myhealth;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPassWordEditText;

    private Button mSignInButton;

    private TextView mRegisterTextView;
    private TextView mErrorTextView;

    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }

        mEmailEditText = (EditText) findViewById(R.id.email_editText);
        mPassWordEditText = (EditText) findViewById(R.id.password_editText);

        mSignInButton = (Button) findViewById(R.id.signin_button);
        mErrorTextView = (TextView) findViewById(R.id.ErrorTextView);
        mRegisterTextView = (TextView) findViewById(R.id.register_textView);
        mRegisterTextView.setPaintFlags(mRegisterTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty()) {
                    return;
                } else {
                    inProgress(true);
                    mAuth.signInWithEmailAndPassword(mEmailEditText.getText().toString(),
                            mPassWordEditText.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            inProgress(false);
                            mErrorTextView.setText("Email atau password salah");
                        }
                    });
                }
            }
        });
    }

    private void inProgress(boolean x){
        if (x) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSignInButton.setEnabled(false);
            mRegisterTextView.setEnabled(false);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSignInButton.setEnabled(true);
            mRegisterTextView.setEnabled(true);
        }
    }

    private boolean isEmpty() {
        if (TextUtils.isEmpty(mEmailEditText.getText().toString())){
            mEmailEditText.setError("Required!!");
            return true;
        }
        if (TextUtils.isEmpty(mPassWordEditText.getText().toString())){
            mPassWordEditText.setError("Required!!");
            return true;
        }
        return false;
    }

    public void registerAkun(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){

    }
}
