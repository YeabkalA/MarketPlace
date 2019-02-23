package com.example.yeabkalwubshit.marketplace;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthFunctions {
    private static FirebaseAuth mAuth;

    public static Boolean runAuthentication(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        Task<AuthResult> result = mAuth.signInWithEmailAndPassword(email, password);
        try {
            while(!result.isComplete()) { Thread.sleep(100); }
        } catch(InterruptedException e) {}

        return result.isSuccessful();
    }
}
