package fyp.fourthyear.cit.ie.watchit.Views;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import fyp.fourthyear.cit.ie.watchit.R;

public class Login extends AppCompatActivity {
    private final String TAG="LOGIN FIREBASE AUTH";

    //Firebase Variables
    //Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    logger(TAG,"onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(Login.this, MainMenu.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                    logger(TAG,"onAuthStateChanged:signed_out");
                    ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                    String currentActivity = am.getAppTasks().get(0).getTaskInfo().topActivity.getClassName();
                    if(currentActivity.contains("MainMenu"))
                    {
                        Toast.makeText(Login.this, "You have been signed out",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    }

                }
            }
        };
        setUpOnClicks();
    }

    private void setUpOnClicks(){
        Button loginBtn = (Button) findViewById(R.id.login);
        Button registerBtn = (Button) findViewById(R.id.login_register);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailET = (EditText) findViewById(R.id.login_email_ET);
                if (emailET.length() == 0) {
                    Toast.makeText(Login.this, "Email not filled out", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText passwordET = (EditText) findViewById(R.id.login_password_ET);
                if (passwordET.length() == 0) {
                    Toast.makeText(Login.this, "Password not filled out", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email=emailET.getText().toString();

                if(!isValidEmail(email))
                {
                    Toast.makeText(Login.this, "Email not valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = passwordET.getText().toString();
                if(password.length()<8)
                {
                    Toast.makeText(Login.this, "Password is too short, it must be at least 8 characters long",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!password.matches(".*\\d+.*"))
                {
                    Toast.makeText(Login.this, "Password must contain at least 1 number",Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                                    Toast.makeText(Login.this, "Login failed",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();

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
