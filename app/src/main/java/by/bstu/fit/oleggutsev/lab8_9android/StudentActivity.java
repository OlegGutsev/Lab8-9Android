package by.bstu.fit.oleggutsev.lab8_9android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class StudentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "StudentActivity";
    private final String ADMINISTRATIONID = "kPBgnQ3YQGdYFsxihrqCpq29Y3v1";

    private EditText ETname;
    private EditText ETsurname;
    private EditText ETmoreInfo;
    private EditText ETmark;

    private ImageView imageView;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Firebase mRoofRef;

    private Uri mImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Firebase.setAndroidContext(this);

        ETname = (EditText) findViewById(R.id.Name_editText);
        ETsurname = (EditText) findViewById(R.id.Surname_editText);
        ETmoreInfo = (EditText) findViewById(R.id.addInformation_editText);
        ETmark = (EditText) findViewById(R.id.Mark_editText);

        imageView = (ImageView) findViewById(R.id.imageView);

        mProgressDialog = new ProgressDialog(this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mRoofRef = new Firebase("https://lab8-9android.firebaseio.com/").child("Users").push();
        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://lab8-9android.appspot.com/");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InformationActivity.class);
        intent.putExtra("userId", getIntent().getStringExtra("userId"));
        startActivity(intent);
        finish(); // закрываем эту активити
    }

    public void LoadImage_onClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();

            imageView.setImageURI(mImageUri);
            mProgressDialog.setMessage("Загрузка изображения....");
            mProgressDialog.show();

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    mProgressDialog.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 2000);
        }
    }

    public void Save_onClick(View view) {
        mProgressDialog.setMessage("Загрузка данных....");
        mProgressDialog.show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                mProgressDialog.cancel();
            }
        };

        final String name = ETname.getText().toString();
        final String surname = ETsurname.getText().toString();
        final String moreInfo = ETmoreInfo.getText().toString();
        final String mark = ETmark.getText().toString();

        if (!name.isEmpty() && !surname.isEmpty() &&
                !moreInfo.isEmpty() && !mark.isEmpty() && !mImageUri.toString().isEmpty()) {

            StorageReference filePath = mStorageReference
                    .child("User_Images")
                    .child(mImageUri.getLastPathSegment());

            filePath.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri photoUri = taskSnapshot.getDownloadUrl();
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            if (user != null) {
                                if(user.getUid().equals(ADMINISTRATIONID))
                                {
                                    writeNewUser(getIntent().getStringExtra("userId"),
                                            name, surname, moreInfo, mark, photoUri.toString());
                                } else {
                                    writeNewUser(user.getUid(), name, surname, moreInfo, mark, photoUri.toString());
                                }
                            }

                            Handler pdCanceller = new Handler();
                            pdCanceller.postDelayed(progressRunnable, 1);
                            toastMessage("Данные успешно добавлены!");
                        }
                    });
        } else toastMessage("Не все поля заполнены!");
    }

    private void writeNewUser(String userId, String name, String surname,
                              String moreInfo, String mark, String photoUri) {

        Student student = new Student(name, surname, moreInfo, mark, photoUri, userId);
        mDatabaseReference.child("Students").child(userId).setValue(student);
    }

    private void toastMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}
