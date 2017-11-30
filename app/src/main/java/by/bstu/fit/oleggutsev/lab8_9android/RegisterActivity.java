package by.bstu.fit.oleggutsev.lab8_9android;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;
    private EditText ETrepeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        ETemail = (EditText) findViewById(R.id.Email);
        ETpassword = (EditText) findViewById(R.id.Password);
        ETrepeatPassword = (EditText) findViewById(R.id.RepeatPassword);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in " + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:out_in ");
                }
            }
        };
    }

    public void onClick(View view) {
        if (view.getId() == R.id.buttonInput) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (view.getId() == R.id.buttonRegister) {

            String email = ETemail.getText().toString();
            String password = ETpassword.getText().toString();
            String repeatPassword = ETrepeatPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty() && !repeatPassword.isEmpty()) {
                if (password.equals(repeatPassword)) {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {

                                        Exception firebaseAuthException = task.getException();

                                        Log.d(TAG, "onComplete: " + firebaseAuthException.getMessage());
                                        toastMessage("Ошибка регистрации!");

                                    } else {
                                        toastMessage("Пользователь успешно зарегестрирован!");
                                    }
                                }
                            });

                } else {
                    toastMessage("Пароли не совпадают");
                }
            } else {
                toastMessage("Не все поля заполнены");
            }
        }
    }


    private void toastMessage(String str) {
        Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
