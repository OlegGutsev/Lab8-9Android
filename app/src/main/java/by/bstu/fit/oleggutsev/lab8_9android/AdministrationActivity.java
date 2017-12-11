package by.bstu.fit.oleggutsev.lab8_9android;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdministrationActivity extends ActionBarActivity {

    private final int MENU_CHANGE = 1;
    private static final String TAG = "AdministrationActivity";

    private RecyclerView recyclerView;
    private StudentsAdapter mStudentsAdapter;
    private List<Student> mStudentList = new ArrayList<>();

    private EditText mSearch;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Oleg Gutsev").withEmail("oleg@mail.ru").withIcon(getResources().getDrawable(R.drawable.logotype))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withBadge("99").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(2),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).withSelectable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)
                ).withOnDrawerListener(new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                // Скрываем клавиатуру при открытии Navigation Drawer
                InputMethodManager inputMethodManager = (InputMethodManager) AdministrationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(AdministrationActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }
        }).build();


        mStudentsAdapter = new StudentsAdapter(getApplicationContext(), mStudentList, new StudentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Student item) {
                Intent intent = new Intent(AdministrationActivity.this, InformationActivity.class);
                intent.putExtra("userId", item.getKey());
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mStudentsAdapter);

        mSearch = (EditText) findViewById(R.id.search_badge);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
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

        onPrepareData();

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }


    @Override
    public void onBackPressed() {
        showDialog(1);
    }

    private void exit() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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

    private void filter(String text) {
        List<Student> temp = new ArrayList();
        for (Student student : mStudentList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if ((student.getName() + " " + student.getSurname()).contains(text)) {
                temp.add(student);
            }
        }
        //update recyclerview
        mStudentsAdapter.updateList(temp);
    }

    private void onPrepareData() {
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
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private int getItemIndex(Student student) {
        int index = -1;

        for (int i = 0; i < mStudentList.size(); i++) {
            if (mStudentList.get(i).getKey().equals(student.getKey())) {
                index = i;
            }
        }

        return index;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == MENU_CHANGE) {
            changeStudent();
        } else {
            removeStudent(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void changeStudent() {
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
        finish();
    }

    private void removeStudent(int position) {

        mDatabaseReference.child("Students")
                .child(mStudentList.get(position).getKey())
                .removeValue();

        mStudentList.remove(position);
        mStudentsAdapter.notifyDataSetChanged();
    }

    private void ShowData(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {

            String key = data.getValue(Student.class).getKey();
            String name = data.getValue(Student.class).getName();
            String surname = data.getValue(Student.class).getSurname();
            String mark = data.getValue(Student.class).getMark();
            String photoId = data.getValue(Student.class).getPhotoUri();

            Student student = new Student();
            student.setKey(key);
            student.setName(name);
            student.setSurname(surname);
            student.setMark(mark);
            student.setPhotoUri(photoId);

            mStudentList.add(student);
            mStudentsAdapter.notifyDataSetChanged();
        }
    }

    private void toastMessage(String str) {
        Toast.makeText(AdministrationActivity.this, str, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.setGroupVisible(R.id.groupVisibleChange, false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.SortItem) {
            SortList();
        } else if (item.getItemId() == R.id.ExitItem) {
            showDialog(1);
        } else if (item.getItemId() == R.id.ListItem) {
            initListDisplay();
        } else {
            initGridDisplay();
        }
        return true;
    }

    // Display a list
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void initListDisplay() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // recyclerView.setAdapter(mStudentsAdapter);
    }

    // Display the Grid
    private void initGridDisplay() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void SortList() {
        List<Student> temp = mStudentList;
        temp.sort(new StudentsComparator());
        mStudentsAdapter.updateList(temp);
    }

    class StudentsComparator implements Comparator<Student> {
        @Override
        public int compare(Student o1, Student o2) {

            return (o1.getName() + " " + o1.getSurname())
                    .compareTo(o2.getName() + " " + o2.getSurname());
        }
    }
}
