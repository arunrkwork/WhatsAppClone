package kapp.chat.activities;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import kapp.chat.R;
import kapp.chat.db.KEY;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;

public class SigninActivity extends BaseActivity implements View.OnClickListener {

    // clear
    Prefs prefs = null;
    int OTP_VERIFIED = 0;

    // for agree layout
    AppCompatButton btnAgree = null;

    //for mobile number layout
    AppCompatSpinner spCountry = null;
    TextView txtError = null;
    ProgressBar progressBar = null;
    EditText edMobile = null, edCountryCode = null;
    AppCompatButton btnSendOtp = null;

    //for otp layout
    AppCompatButton btnOtpVerify = null;
    EditText edOtp = null;
    TextView txtEnter = null, txtWaiting = null, txtMobile = null;


    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private static final String KEY_VERIFY_LAYOUT = "key_verify_layout";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private int mCURRENT_LAYOUT = 1;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_VERIFY_LAYOUT, mCURRENT_LAYOUT);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCURRENT_LAYOUT = savedInstanceState.getInt(KEY_VERIFY_LAYOUT);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore instance state
        if (savedInstanceState != null) {
            L.i("SigninActivity calling... onRestoreInstanceState");
            onRestoreInstanceState(savedInstanceState);
        }

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        prefs = new Prefs(this);
        OTP_VERIFIED = prefs.getInt(Prefs.PREF_KEY_OTP_VERIFIED);
        if (OTP_VERIFIED == 0) {
            if (mCURRENT_LAYOUT == 1) setAgreeView();
            else if (mCURRENT_LAYOUT == 2) setMobileNumberInputContentView();
            else if (mCURRENT_LAYOUT == 3) setOtpInputContentView();
        } else startActivity();

    }

    private void setAgreeView() {
        setContentView(R.layout.activity_signin);
        mCURRENT_LAYOUT = 1;
        btnAgree = findViewById(R.id.btnAgree);
        btnAgree.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAgree:
                setMobileNumberInputContentView();
                break;
            case R.id.btnSendOtp:
                if (!validatePhoneNumber()) {
                    return;
                }
                disableView(spCountry, edCountryCode, edMobile, btnSendOtp);
                visibleViews(progressBar);
                startPhoneNumberVerification(edMobile.getText().toString());
                break;
            case R.id.btnOtpVerify:
                String code = edOtp.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    edOtp.setError("Cannot be empty.");
                    return;
                }
                disableView(btnOtpVerify, edOtp, txtEnter);
                visibleViews(progressBar);
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
        }
    }

    private void visibleViews(View... views) {
        for (View view : views) if (view != null) view.setVisibility(View.VISIBLE);
    }

    private void disableView(View... views) {
        for (View view : views) if (view != null) view.setVisibility(View.GONE);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }


    private void setOtpInputContentView() {
        setContentView(R.layout.layout_otp_verification);
        mCURRENT_LAYOUT = 3;
        progressBar = findViewById(R.id.progressBar);
        txtWaiting = findViewById(R.id.txtWaiting);
        txtWaiting.setText("Otp sent this " + prefs.getString(KEY.MOBILE_NO) + " number.");
        txtEnter = findViewById(R.id.txtEnter);
        txtMobile = findViewById(R.id.txtMobile);
        txtMobile.setText("Verify " + prefs.getString(KEY.MOBILE_NO));
        edOtp = findViewById(R.id.edOtp);
        btnOtpVerify = findViewById(R.id.btnOtpVerify);
        btnOtpVerify.setOnClickListener(this);
    }

    private void setMobileNumberInputContentView() {
        setContentView(R.layout.layout_mobile_number_login);
        mCURRENT_LAYOUT = 2;
        progressBar = findViewById(R.id.progressBar);
        edCountryCode = findViewById(R.id.edCountryCode);
        edMobile = findViewById(R.id.edMobile);

        edMobile.requestFocus();
        edMobile.setSelection(edMobile.getText().length());

        spCountry = findViewById(R.id.spCountry);
        txtError = findViewById(R.id.txtError);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnSendOtp.setOnClickListener(this);

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                L.d("SigninActivity onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                reEnableMobileView();
                L.w("SigninActivity onVerificationFailed", e);
                L.t(getApplicationContext(), "Process failed...");
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                L.d("SigninActivity onCodeSent:" + verificationId);
                setOtpInputContentView();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            L.d("SigninActivity signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            //update ui
                            setMainView(user.getUid());

                        } else {
                            // Sign in failed, display a message and update the UI
                            L.w("SigninActivity signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                L.t(getApplicationContext(), "The verification code entered was invalid");
                                // [END_EXCLUDE]
                            } else if (task.getException() instanceof FirebaseNetworkException) {
                                L.t(getApplicationContext(), "Network Error...");
                                if (mCURRENT_LAYOUT == 2) reEnableMobileView();
                                if (mCURRENT_LAYOUT == 3) reEnableOtpView();
                                //update ui
                            }
                        }
                    }
                });
    }

    private void reEnableOtpView() {
        visibleViews(btnOtpVerify, edOtp, txtEnter);
        disableView(progressBar);
    }


    private void reEnableMobileView() {
        visibleViews(spCountry, edCountryCode, edMobile, btnSendOtp);
        disableView(progressBar);
        edMobile.requestFocus();
        edMobile.setSelection(edMobile.getText().length());
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = edMobile.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            edMobile.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        phoneNumber = "+91" + phoneNumber;
        prefs.putString(KEY.MOBILE_NO, phoneNumber);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        if (mCURRENT_LAYOUT == 2) {
            L.i("SigninActivity onstart called...");
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            //update UI
            if (currentUser != null) setMainView(currentUser.getUid());

            // [START_EXCLUDE]
            if (mVerificationInProgress && validatePhoneNumber()) {
                L.i("SigninActivity ,mVerificationInProgress is true");
                startPhoneNumberVerification(edMobile.getText().toString());
            }
            // [END_EXCLUDE]
        }
        // [END on_start_check_user]
    }

}
