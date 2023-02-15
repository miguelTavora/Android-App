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

import dam_path.dam_45102.storage.UserInfo;

public class CreateGroup extends AppCompatActivity {

    private DatabaseReference dbRef;
    private StorageReference imagesRef;

    //variaveis para submeter imagem na db
    private StorageReference imgStorage;
    private Uri file;

    private ImageView imgGroup;
    private EditText groupName;
    private EditText groupDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        dbRef = FirebaseDatabase.getInstance().getReference();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        imagesRef = storageRef.child("group");

        imgStorage = null;

        imgGroup = findViewById(R.id.groupImg);
        groupName = findViewById(R.id.groupName);
        groupDescription = findViewById(R.id.groupDescription);

        checkUserColor();


        TextView chooseImg = findViewById(R.id.chooseImg);
        Button submit = findViewById(R.id.btn_submit);
        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        chooseImg.setOnClickListener(new View.OnClickListener() {
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
                createGroup();
            }

        });
    }

    private void createGroup() {
        final String value = groupName.getText().toString();
        if (value.length() > 1 && MainActivity.checkAlphaNumeric(value) && value.length() < 16) {
            dbRef.child(UserInfo.GROUPS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(value)) {
                        String groupDescription = getGroupDescription();
                        if (groupDescription != null) {
                            if (imgStorage != null) {
                                submitImageToDB(value, groupDescription);
                            } else
                                Toast.makeText(CreateGroup.this, "Please choose a group image", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(CreateGroup.this, "The description must contain between 4 and 20 characters", Toast.LENGTH_LONG).show();

                    } else
                        Toast.makeText(CreateGroup.this, "The name you wrote already exists please try a new one", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("cancel", "onCancelled", error.toException());
                }
            });

        } else
            Toast.makeText(CreateGroup.this, "The name must contain only letters and have numbers and between 2 and 15 characters", Toast.LENGTH_LONG).show();
    }

    private String getGroupDescription() {
        String value = groupDescription.getText().toString();
        if (value.length() > 3 && value.length() < 21) {
            return value;
        }
        return null;
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
                Toast.makeText(CreateGroup.this, "Request denied..", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UserInfo.IMAGE_PICK_CODE) {
            imgGroup.setImageURI(data.getData());

            file = data.getData();

            assert file != null;
            imgStorage = imagesRef.child("images" + file.getLastPathSegment());


        }
    }

    private void submitImageToDB(final String name, final String description) {
        imgStorage.putFile(file).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(CreateGroup.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dbRef.child(UserInfo.GROUPS_ACCESS).child(name).child(UserInfo.IMAGE_GROUPS_ACCESS).setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dbRef.child(UserInfo.GROUPS_ACCESS).child(name).child(UserInfo.DESCRIPTION_GROUPS_ACCESS).setValue(description);
                                dbRef.child(UserInfo.GROUPS_ACCESS).child(name).child(UserInfo.ADMIN_GROUPS_ACCESS).setValue(UserInfo.username);
                                dbRef.child(UserInfo.GROUPS_ACCESS).child(name).child(UserInfo.MEMBERS_GROUPS_ACCESS).child(UserInfo.username).setValue(true);
                                Toast.makeText(CreateGroup.this, "Success creating the group", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CreateGroup.this, Group.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }

        });
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

        if(color == 1) {
            lin.setBackgroundResource(R.color.black_clean);
        }
        else if (color == 2) {
            lin.setBackgroundResource(R.color.red3);
        }
        else if (color == 3){
            lin.setBackgroundResource(R.color.blue);
        }
    }
}
