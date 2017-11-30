package by.bstu.fit.oleggutsev.lab8_9android;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final String ADMINISTRATIONID = "kPBgnQ3YQGdYFsxihrqCpq29Y3v1";


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ETemail = (EditText) findViewById(R.id.Email);
        ETpassword = (EditText) findViewById(R.id.Password);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if (user.getUid().equals(ADMINISTRATIONID)) {

                        Intent intent = new Intent(MainActivity.this, AdministrationActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "onAuthStateChanged:signed_in " + user.getUid());
                        finish();
                    } else {

                        Intent intent = new Intent(MainActivity.this, InformationActivity.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onAuthStateChanged:signed_in " + user.getUid());
                        finish();
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:out_in ");
                }
            }
        };
    }

    public void Registration_onClick(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 1);
        finish();
    }

    public void Enter_onClick(View view) {
        String email = ETemail.getText().toString();
        String password = ETpassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            Signing(email, password);
        } else {
            toastMessage("Не все поля заполнены");
        }
    }

    public void Signing(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    toastMessage("Ошибка Авторизации");
                }
            }
        });
    }

    private void toastMessage(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
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
