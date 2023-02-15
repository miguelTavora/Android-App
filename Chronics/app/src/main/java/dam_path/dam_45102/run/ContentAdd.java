package dam_path.dam_45102.run;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dam_path.dam_45102.storage.Publication;
import dam_path.dam_45102.storage.UserInfo;

public class ContentAdd extends AppCompatActivity {

    private EditText txtWritten;
    private DatabaseReference dbRef;

    private ImageView profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add);

        Button btnSubmit = findViewById(R.id.btn_submit);
        txtWritten = findViewById(R.id.text_writer);
        dbRef = FirebaseDatabase.getInstance().getReference();


        ImageButton homeBtn = findViewById(R.id.home);
        ImageButton searchBtn = findViewById(R.id.search);
        ImageButton addBtn = findViewById(R.id.add);
        ImageButton rankBtn = findViewById(R.id.rank);
        profileBtn = findViewById(R.id.profile);

        setProfilePic();
        checkUserColor();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = validateStoryWritten();
                if(!result.equals("Success submitting the story"))
                    Toast.makeText(ContentAdd.this, result, Toast.LENGTH_SHORT).show();
                addPublicationToBD();
            }

        });


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentAdd.this, Content.class);
                startActivity(intent);
            }

        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentAdd.this, Group.class);
                startActivity(intent);
            }

        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentAdd.this, ContentAdd.class);
                startActivity(intent);
            }

        });

        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentAdd.this, Content.class);
                intent.putExtra("rank", true);
                startActivity(intent);
            }

        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentAdd.this, Profile.class);
                startActivity(intent);
            }

        });
    }

    private String validateStoryWritten() {
        String txt = txtWritten.getText().toString();

        if (txt.length() < 15) {
            return "The story must contain more than 15 characters";
        } else if (txt.length() > 1600) {
            return "The story must contain less than 1600 characters";
        } else {
            return "Success submitting the story";
        }
    }

    private void addPublicationToBD() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Publication pub = new Publication(txtWritten.getText().toString(), formattedDate);

        checkCurrentIndex(pub);
    }


    private void checkCurrentIndex(final Publication pub) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(UserInfo.PUBLICATIONS_ACCESS)) {
                    dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long index = dataSnapshot.getChildrenCount();
                            dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(UserInfo.username).child("" + index).setValue(pub);
                            dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(UserInfo.username).child("" + index).child(UserInfo.TIMER_ACCESS).setValue(""+System.currentTimeMillis());
                            Toast.makeText(ContentAdd.this, "Success submitting the story", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ContentAdd.this, Content.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("cancel", "onCancelled", error.toException());
                        }
                    });
                }
                else {
                    dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(UserInfo.username).child("0").setValue(pub);
                    dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(UserInfo.username).child("0").child(UserInfo.TIMER_ACCESS).setValue(""+System.currentTimeMillis());
                    Toast.makeText(ContentAdd.this, "Success submitting the story", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ContentAdd.this, Content.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
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
                            Picasso.with(ContentAdd.this).load(String.valueOf(dataSnapshot.getValue())).fit().centerInside().into(profileBtn);
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

    private void checkUserColor(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.LAYOUT_COLOR_ACCESS)){
                    setColorToTop((Long)dataSnapshot.child(UserInfo.LAYOUT_COLOR_ACCESS).getValue());
                    setColorToBottom((Long)dataSnapshot.child(UserInfo.LAYOUT_COLOR_ACCESS).getValue());
                }
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

    private void setColorToBottom(long color){
        LinearLayout lin = findViewById(R.id.bottomBar);
        LinearLayout lin2 = findViewById(R.id.bottomBar2);

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
