package dam_path.dam_45102.run;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import dam_path.dam_45102.storage.UserInfo;

public class EditProfile extends AppCompatActivity {

    private DatabaseReference dbRef;
    private ImageView img;
    private StorageReference imagesRef;
    private EditText bio;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        ImageButton backBtn = findViewById(R.id.back);
        TextView choosePhoto = findViewById(R.id.choosePhoto);
        img = findViewById(R.id.backImg);
        bio = findViewById(R.id.bio);
        dbRef = FirebaseDatabase.getInstance().getReference();
        Button submit = findViewById(R.id.btn_submit);
        TextView changeLayout = findViewById(R.id.changeLayout);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        imagesRef = storageRef.child("profile");
        color = -1;

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            int color = extras.getInt("rank");
            setColorDetect(color);

        }

        setProfilePic();
        setUsername();
        setBio();
        checkUserColor();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
            }


        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verificar runtime permissão
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        //sem permissão, vai pedir
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, UserInfo.PERMISSION_CODE);

                    } else {
                        //tem permissão
                        pickImageFromGallery();
                    }
                } else {
                    //sistema menos do marsmello
                    pickImageFromGallery();
                }
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitContent();
            }
        });

        changeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, LayoutChange.class);
                startActivity(intent);
            }
        });
    }


    private void setProfilePic(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                    dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.PROFILE_PICTURE_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Picasso.with(EditProfile.this).load(String.valueOf(dataSnapshot.getValue())).fit().centerInside().into(img);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("cancel", "onCancelled", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void setUsername(){
        EditText txt = findViewById(R.id.username);
        txt.setText(UserInfo.username);
    }

    private void setBio(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.BIO_ACCESS)){
                    dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.BIO_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            EditText txt = findViewById(R.id.bio);
                            txt.setText((String)dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("cancel", "onCancelled", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, UserInfo.IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == UserInfo.PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                pickImageFromGallery();
            else
                Toast.makeText(EditProfile.this, "Request denied..", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UserInfo.IMAGE_PICK_CODE) {
            final Intent copy = data;

            Uri file = data.getData();

            assert file != null;
            final StorageReference imgStorage = imagesRef.child("images" + file.getLastPathSegment());

            imgStorage.putFile(file).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(EditProfile.this, "Request failed", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.PROFILE_PICTURE_ACCESS).setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditProfile.this, "Success submitting the photo", Toast.LENGTH_LONG).show();
                                    img.setImageURI(copy.getData());
                                }
                            });
                        }
                    });
                }

            });
        }
    }


    private void submitContent(){
        if(bio.getText().toString().length() < 160 && bio.getText().toString().length() > 0) {
            dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.BIO_ACCESS).setValue(bio.getText().toString());
            Toast.makeText(EditProfile.this, "Bio changed with success", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(EditProfile.this, "Bio must have less than 160 characters and can't be blanked", Toast.LENGTH_LONG).show();
        }
    }

    private void checkUserColor(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.LAYOUT_COLOR_ACCESS))
                    setColorToTop((Long)dataSnapshot.child(UserInfo.LAYOUT_COLOR_ACCESS).getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void setColorToTop(long color){
        LinearLayout lin = findViewById(R.id.topBar);
        LinearLayout lin2 = findViewById(R.id.topBar2);

        if(color == 1) {
            lin.setBackgroundResource(R.color.black_clean);
            lin2.setBackgroundResource(0);
        }
        else if (color == 2) {
            lin.setBackgroundResource(R.color.red3);
            lin2.setBackgroundResource(0);
        }
        else if (color == 3){
            lin.setBackgroundResource(R.color.blue);
            lin2.setBackgroundResource(0);
        }
    }

    private void setColorDetect(int color){
        LinearLayout lin = findViewById(R.id.topBar);
        LinearLayout lin2 = findViewById(R.id.topBar2);

        if(color == 0) {
            lin.setBackgroundResource(R.color.black_clean);
            lin2.setBackgroundResource(R.drawable.text_lines);
        }

        if(color == 1) {
            lin.setBackgroundResource(R.color.black_clean);
            lin2.setBackgroundResource(0);
        }
        else if (color == 2) {
            lin.setBackgroundResource(R.color.red3);
            lin2.setBackgroundResource(0);
        }
        else if (color == 3){
            lin.setBackgroundResource(R.color.blue);
            lin2.setBackgroundResource(0);
        }
    }

}
