package by.bstu.fit.oleggutsev.lab8_9android;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;


public class InformationActivity extends AppCompatActivity {

    private static final String TAG = "InformationActivity";
    private final String ADMINISTRATIONID = "kPBgnQ3YQGdYFsxihrqCpq29Y3v1";

    private TextView mTextView;
    private TextView mNameAndSurname;
    private TextView mMark;
    private TextView mAddInformation;

    private ImageView image;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mTextView = (TextView) findViewById(R.id.Info_textView);
        image = (ImageView) findViewById(R.id.image);
        mNameAndSurname = (TextView) findViewById(R.id.nameAndSurname);
        mMark = (TextView) findViewById(R.id.mark);
        mAddInformation = (TextView) findViewById(R.id.addInfo);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        mDatabaseReference = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in " + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out ");
                }
            }
        };


        //  InformationActivity.this.startActionMode(mAction);
        updateInformation();
    }

    @Override
    public void onBackPressed() {
        if (userId.equals(ADMINISTRATIONID)) {
            Intent intent = new Intent(this, AdministrationActivity.class);
            startActivity(intent);
            finish();
        } else {
            showDialog(1);
        }
    }


    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            // заголовок
            adb.setTitle("Выход");
            // сообщение
            adb.setMessage("Вы точно хотите выйти?");
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adb.setPositiveButton("Да", myClickListener);
            // кнопка отрицательного ответа
            adb.setNegativeButton("Нет", myClickListener);

            // создаем диалог
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    exit();
                    break;
                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private void exit() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateInformation() {
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ShowData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    private void ShowData(DataSnapshot dataSnapshot) {

        if (userId.equals(ADMINISTRATIONID)) {
            getInformation(dataSnapshot.child(getIntent().getStringExtra("userId")));
        } else {
            getInformation(dataSnapshot.child(userId));
        }
    }

    private void getInformation(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            Map<String, String> map = dataSnapshot.getValue(Student.class).toMap();

            String name = map.get("name");
            String surname = map.get("surname");
            String mark = map.get("mark");
            String moreInfo = map.get("moreInfo");

            mNameAndSurname.setText(name + " " + surname);
            mMark.setText(mark);
            mAddInformation.setText(moreInfo);

            Glide.with(getApplicationContext()).load(map.get("photoId")).into(image);

        } else {
            mTextView.setVisibility(View.VISIBLE);
            mNameAndSurname.setVisibility(View.GONE);
            mMark.setVisibility(View.GONE);
            mAddInformation.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
        }
    }

    private void toastMessage(String str) {
        Toast.makeText(InformationActivity.this, str, Toast.LENGTH_SHORT).show();
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


    public void onClick(View view) {
        showDialog(1);
    }


    public void ChangeInfo_onClick(View view) {
        ChangeActivity();
    }

    private void ChangeActivity() {
        Intent intent = new Intent(this, StudentActivity.class);
        if (userId.equals(ADMINISTRATIONID)) {
            intent.putExtra("userId", getIntent().getStringExtra("userId"));
        }
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.setGroupVisible(R.id.groupVisibleSort, false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.EditItem) {
            ChangeActivity();
        } else if (item.getItemId() == R.id.ExitItem) {
            showDialog(1);
        }

        return true;
    }

    private void Copy() {
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipboardManager.OnPrimaryClipChangedListener clipListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                CharSequence text = item.getText();

                ClipData clip = ClipData.newPlainText("label", text);
                clipboard.setPrimaryClip(clip);
            }
        };
    }

    private void Send() {
    }
}