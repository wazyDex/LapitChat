package com.example.android.lapitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    public Button mCreateButton;
    private FirebaseAuth mAuth;

    private ProgressDialog mRegProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();



        // Registration fields
        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateButton = (Button) findViewById(R.id.reg_create_btn);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();



                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(display_name, email, password);
                }

            }
        });


    }

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> usermap = new HashMap<>();
                    usermap.put("name", display_name);
                    usermap.put("status", "HI Im Using The App");
                    usermap.put("image", "default");
                    usermap.put("thumb_image", "default");

                    mDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                mRegProgress.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }


                        }
                    });



                } else{
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "You got and error password min 8 char", Toast.LENGTH_LONG).show();
                }


            }
        });

    }
}
