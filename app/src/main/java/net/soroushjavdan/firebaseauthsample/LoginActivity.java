package net.soroushjavdan.firebaseauthsample;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by soroush on 8/16/16.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button loginBtn;
    private TextView forgotPassTxt;
    private TextView registerTxt;
    private ProgressBar mProgressView;

    private FirebaseAuth auth ;
    private int googlePlayStatus ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        mEmailView = (EditText) findViewById(R.id.log_in_email_input);
        mPasswordView = (EditText) findViewById(R.id.log_in_password_input);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        loginBtn = (Button) findViewById(R.id.log_in_button);
        registerTxt = (TextView) findViewById(R.id.register_txt);
        forgotPassTxt = (TextView) findViewById(R.id.forgot_pass_txt);

        loginBtn.setOnClickListener(clickListener);
        registerTxt.setOnClickListener(clickListener);
        forgotPassTxt.setOnClickListener(clickListener);

        mProgressView = (ProgressBar) findViewById(R.id.log_in_progress);

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.register_txt:
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    break;
                case R.id.forgot_pass_txt:
                    startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
                    break;
                case R.id.log_in_button:
                    attemptLogin();
                    break;
            }
        }
    };

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

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
        } else if(googlePlayStatus != ConnectionResult.SUCCESS){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, 10);
            if(dialog != null)
                dialog.show();
        }else{
            mProgressView.setVisibility(View.VISIBLE);
            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            mProgressView.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                Toast.makeText(LoginActivity.this, "login failed due to "+task.getException() , Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Successfully login" , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
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
