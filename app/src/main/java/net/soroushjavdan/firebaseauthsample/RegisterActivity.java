package net.soroushjavdan.firebaseauthsample;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity{

    // UI references
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;

    private FirebaseAuth auth ;
    private int googlePlayStatus ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Set up the register form.
        mEmailView = (EditText) findViewById(R.id.register_email_input);
        mPasswordView = (EditText) findViewById(R.id.register_password_input);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.register_button);
        TextView loginBtn = (TextView) findViewById(R.id.log_in_txt);
        TextView forgotPassBtn = (TextView) findViewById(R.id.forgot_pass_txt);
        mProgressView = findViewById(R.id.register_progress);

        mEmailRegisterButton.setOnClickListener(clickListener);
        loginBtn.setOnClickListener(clickListener);
        forgotPassBtn.setOnClickListener(clickListener);

    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.log_in_txt:
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    break;
                case R.id.forgot_pass_txt:
                    startActivity(new Intent(RegisterActivity.this, ForgotPassActivity.class));
                    break;
                case R.id.register_button:
                    attemptRegister();
                    break;
            }
        }
    };

    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the register attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }else if(googlePlayStatus != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, 10);
            if(dialog != null)
                dialog.show();
        }else{
            mProgressView.setVisibility(View.VISIBLE);
            //authenticate user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressView.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration failed due to " + task.getException(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Successfully registered" , Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            }
                        }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

}

