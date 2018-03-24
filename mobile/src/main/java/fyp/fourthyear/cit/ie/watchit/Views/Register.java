package fyp.fourthyear.cit.ie.watchit.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import fyp.fourthyear.cit.ie.watchit.R;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final String TAG="REGISTER FIREBASE AUTH";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        setUpOnClicks();
    }
    private void setUpOnClicks()
    {

        Button registerBtn = (Button) findViewById(R.id.register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText nameET = (EditText) findViewById(R.id.register_name_ET);
                if(nameET.length() == 0){
                    Toast.makeText(Register.this,"Name not filled out", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText emailET = (EditText) findViewById(R.id.register_email_ET);
                if (emailET.length() == 0) {
                    Toast.makeText(Register.this, "Email not filled out", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText passwordET = (EditText) findViewById(R.id.register_password_ET);
                if (passwordET.length() == 0) {
                    Toast.makeText(Register.this, "Password not filled out", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText confirmPasswordET = (EditText) findViewById(R.id.register_confirm_password_ET);
                if (passwordET.length() == 0){
                    Toast.makeText(Register.this, "Confirm password not filled out", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String name = nameET.getText().toString();

                final String email = emailET.getText().toString();
                if(!isValidEmail(email)) {
                    Toast.makeText(Register.this, "Email not valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String password = passwordET.getText().toString();
                String confirmPassword = confirmPasswordET.getText().toString();
                if(!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(password.length()<8) {
                    Toast.makeText(Register.this, "Password is too short, it must be at least 8 characters long",Toast.LENGTH_LONG).show();
                    return;
                }
                else if(!password.matches(".*\\d+.*")) {
                    Toast.makeText(Register.this, "Password must contain at least 1 number",Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Login failed. "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    logger(TAG,task.getException().getMessage());
                                }
                                else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    database.getReference("Wearers").child(user.getUid()).child("Name").setValue(name);
                                    Toast.makeText(Register.this, "Account registered. Logging you in",Toast.LENGTH_LONG).show();
                                }

                            }
                        });

            }
        });


    }
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    private void logger(String tag, String message)
    {
        Log.d(tag,message);
    }




}
