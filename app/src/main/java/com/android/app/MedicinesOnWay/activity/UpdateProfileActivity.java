package com.android.app.MedicinesOnWay.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.MedicinesOnWay.R;
import com.android.app.MedicinesOnWay.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private static String userId;

    private TextView txtDetails;
    private EditText inputName, inputEmail, inputAddress, inputMobile;
    private Button btnSave;

    Bitmap bitmap;
    private Uri fileUri, cameraImageUri;
    static String image;
    static String type;

    static String name;
    static String email;
    static String mobile;

    ImageView imageView;
    Spinner spinner;
    ImageButton addImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");

        inputName = findViewById(R.id.name);
        inputEmail = findViewById(R.id.email);
        inputAddress = findViewById(R.id.address);
        inputMobile = findViewById(R.id.mobile);
        btnSave = findViewById(R.id.btn_save);
        imageView = findViewById(R.id.image);
        addImageButton = findViewById(R.id.btn_add_image);

        inputName.setText(name);
        inputMobile.setText(mobile);
        inputEmail.setText(email);

        spinner = findViewById(R.id.spinner_type);

        final String[] arraySpinner = new String[]{
                "Normal", "Hearts Relation", "Diabetics", "Brain"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);

        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                type = arraySpinner[position];
                Toast.makeText(UpdateProfileActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("user");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("user");

        // app_title change listener
        mFirebaseInstance.getReference("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("", "App title updated");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("", "Failed to read app title value.", error.toException());
            }
        });

        // Save / update the user
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = inputAddress.getText().toString();

                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createUser(name, email, address, mobile, image, type);
                } else {
                    updateUser(name, email);
                }
            }
        });

        toggleButton();
    }

    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(userId)) {
            btnSave.setText("Save");
        } else {
            btnSave.setText("Update");
        }
    }

    /**
     * Creating new user node under 'users'
     */
    private void createUser(String name, String email, String address, String mobile, String image, String type) {

        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

        User user = new User(name, email, image, type, address, mobile);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */

    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.getName() + ", " + user.getEmail());

                toggleButton();

                userId = null;

                Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                intent.putExtra("image", image);
                intent.putExtra("name", name);
                intent.putExtra("email", email);

                SharedPreferences sharedpreferences = getSharedPreferences("Profile", 0);

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("name", name);
                editor.putString("email", email);
                editor.putString("image", image);
                editor.apply();

                startActivity(intent);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateUser(String name, String email) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(userId).child("name").setValue(name);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabase.child(userId).child("email").setValue(email);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {

            fileUri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            image = imageToString(bitmap);
        }

        if (requestCode == 200 && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");

            imageView.setImageBitmap(bitmap);
            image = imageToString(bitmap);
        }
    }


    private String imageToString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] image = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(image, Base64.DEFAULT);

    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    private void showPictureDialog() {

        final android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);

        pictureDialog.setTitle("Update Your Photo");

        String[] pictureDialogItems = {
                "Select photo from Gallery",
                "Capture photo from Camera",
                "Cancel"};
        pictureDialog.setIcon(R.drawable.ic_photo_library_black_24dp);

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;

                            case 1:
                                takePhotoFromCamera();
                                break;

                            case 2:
                                dialog.dismiss();
                        }
                    }
                });

        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/jpeg");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);


        startActivityForResult(galleryIntent, 100);
    }

    private void takePhotoFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        cameraImageUri = cameraIntent.getData();

        startActivityForResult(cameraIntent, 200);
    }
}