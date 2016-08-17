package net.soroushjavdan.firebaseauthsample;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by soroush on 8/16/16.
 */
public class ForgotPassActivity extends AppCompatActivity {

    // UI references
    private EditText mEmailView;
    private ProgressBar mProgressView;

    private FirebaseAuth auth ;
    private int googlePlayStatus ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_forgotpass);

        auth = FirebaseAuth.getInstance();


        googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());


        mEmailView = (EditText) findViewById(R.id.reset_pass_email_input);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptResetPassword();
                    return true;
                }
                return false;
            }
        });

        Button sendEmailBtn = (Button) findViewById(R.id.reset_pass_button);
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptResetPassword();
            }
        });

        mProgressView = (ProgressBar) findViewById(R.id.reset_pass_progress);
    }

    private void attemptResetPassword() {
        // Reset errors.
        mEmailView.setError(null);

        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();


        }if(googlePlayStatus != ConnectionResult.SUCCESS){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, 10);
            if(dialog != null)
                dialog.show();
        } else {
            mProgressView.setVisibility(View.VISIBLE);
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPassActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ForgotPassActivity.this, "Failed to send reset email ", Toast.LENGTH_SHORT).show();
                            }
                            mProgressView.setVisibility(View.GONE);
                        }
                    });
        }
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
}