package com.project.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.myapplication.R;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
    }

    private boolean isSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is already signedIn or not
        if (isSignedIn()) {
            //If signed in create a timer to call main activity after 3 seconds
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, 3000);

        } else {
            // Else start signIn process
            initSignInProcess();

        }

    }

    private void initSignInProcess() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1060018672946-40ipgpeomre764gse0dpeo9phc95gg8a.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn(googleSignInClient);
    }

    private void signIn(GoogleSignInClient client) {

        Intent signInIntent = client.getSignInIntent();

        ActivityResultLauncher<Intent> signInActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            assert account != null;
                            firebaseAuthWithGoogle(account.getIdToken(),account.getEmail(),account.getGivenName());
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Toast.makeText(SplashActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();


                        }
                    }
                }
        );

        signInActivityResultLauncher.launch(signInIntent);

    }

    private void firebaseAuthWithGoogle(String idToken,String gmail,String name) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseFirestore.getInstance().collection("User").document(gmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (value.exists()) {
                                         address = value.getString("address");
                            HashMap<String,Object> map = new HashMap();
                            map.put("name", name);
                            map.put("address", address);

                            FirebaseFirestore.getInstance().collection("User").document(gmail).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    if (getIntent().getExtras() != null)
                                        intent.putExtras(getIntent().getExtras());
                                    startActivity(intent);
                                    finish();

                                }
                            });

                        } else {

                            // If sign in fails, display a message to the user.
                            assert task.getException() != null;
                            Toast.makeText(SplashActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                        }}});

    }

}