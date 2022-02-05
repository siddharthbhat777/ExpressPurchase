package com.project.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.myapplication.R;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "MainActivity";
    GoogleSignInOptions gso;

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //setup signinmethod
        setupsignin();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(), account.getId(), account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString());
            } catch (ApiException e) {
                Toast.makeText(SplashActivity.this, "Failed ! Something wrong happens", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onActivityResult: " + e.getLocalizedMessage());

            }
        }
    }

    private void setupsignin() {

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // this is not an error! don't worry about this one i am also seeing this warning in my project
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void firebaseAuthWithGoogle(String token, String idToken, String gmail, String name, String photourl) {
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            setcompletedata(token, idToken, gmail, name, photourl); // storing user data in firestore

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SplashActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onActivityResult: " + task.getException().getLocalizedMessage());

                        }
                    }
                });
    }

    private void setcompletedata(String token, String idToken, String gmail, String name, String photourl) {
//        service.execute(new Runnable() {
//            @Override
//            public void run() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(idToken).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    // if the user is already in databse means if the user had already sign n to the app and again uninstall the app but agin he is doing signin to the account
                    // no need to show gender dialog
                 /*Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();*/

                } else {

                    // not in databse , new user

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", name); // storing user name in databse
                    map.put("email", gmail); // storing gmail in database
                    map.put("uid", idToken); // this is the unique id of every user // Note: with this uid u can access ur user complete detail
                    map.put("image", photourl); // storing photo url


                    db.collection("Users").document(idToken).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //show gender dialog
                                /*Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();*/
//                                onStart();
                            }
                        }
                    });
                }
            }
        });
    }
    private void signin() {
        // user is not signed in
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    //        });
//    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // dialog will not show because user is already signin
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(1000); // screen will be visible for 1 second!
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    super.run();
                }
            };thread.start();
        }else{


            signin(); // showing dialog
        }

    }
}