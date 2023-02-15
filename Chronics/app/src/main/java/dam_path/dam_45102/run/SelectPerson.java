package dam_path.dam_45102.run;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import dam_path.dam_45102.storage.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class SelectPerson extends AppCompatActivity {

    final private long[] divisions = new long[]{10,14,15,20,21,25,26,30};
    private LinearLayout layoutMain;
    private DatabaseReference dbRef;
    private ArrayList<CircleImageView> profiles;
    private ArrayList<LinearLayout> talk;
    private ArrayList<TextView> name;
    private ArrayList<TextView> bios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_person);

        dbRef = FirebaseDatabase.getInstance().getReference();
        profiles = new ArrayList<>();
        talk = new ArrayList<>();
        name = new ArrayList<>();
        bios = new ArrayList<>();

        layoutMain = findViewById(R.id.layoutMain);

        setLyoutWithBD();
        checkUserColor();

        ImageButton backBtn = findViewById(R.id.back);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectPerson.this, Conversations.class);
                startActivity(intent);
            }

        });
    }

    private void createLayout(){
        LinearLayout lin = new LinearLayout(this);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
        layParams.setMargins(dpPad10,dpPad10,dpPad10,dpPad10);
        lin.setBackgroundResource(R.drawable.box_aroud);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setWeightSum(6f);
        lin.setLayoutParams(layParams);


        CircleImageView photo = new CircleImageView (this);
        int dpPad50 = (int) UserInfo.convertDpToPixel(50, getApplicationContext());
        LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(0, dpPad50,1f);
        layParams2.gravity = Gravity.CENTER;
        int dpPad2 = (int) UserInfo.convertDpToPixel(2, getApplicationContext());
        layParams2.setMargins(dpPad10,dpPad2,dpPad10,dpPad2);
        photo.setImageResource(R.drawable.profile);
        photo.setLayoutParams(layParams2);

        LinearLayout linLay = new LinearLayout(this);
        LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,5f);
        linLay.setOrientation(LinearLayout.VERTICAL);
        linLay.setLayoutParams(layParams3);

        TextView user = new TextView(this);
        LinearLayout.LayoutParams layParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layParams4.gravity = Gravity.CENTER;
        user.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        user.setText(R.string.exUser1);
        user.setLayoutParams(layParams4);

        TextView bio = new TextView(this);
        bio.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        bio.setLayoutParams(layParams4);

        linLay.addView(user);
        linLay.addView(bio);

        lin.addView(photo);
        lin.addView(linLay);

        name.add(user);
        bios.add(bio);
        profiles.add(photo);
        talk.add(linLay);
        layoutMain.addView(lin);
    }

    private void setLyoutWithBD(){
        dbRef.child(UserInfo.USERS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if((Long)snapshot.child(UserInfo.SOCIAL_POINTS_ACCESS).getValue() != 0 && !Objects.equals(snapshot.getKey(), UserInfo.username)){
                        createLayout();
                        setTextToUser(snapshot.getKey(),(Long)snapshot.child(UserInfo.SOCIAL_POINTS_ACCESS).getValue(),
                                (Long)dataSnapshot.child(UserInfo.username).child(UserInfo.SOCIAL_POINTS_ACCESS).getValue(),count);

                        setBioToUser(snapshot.getKey(),count);
                        setPhotoToUser(snapshot.getKey(),count);
                        setListenersToLayouts(snapshot.getKey(),count);
                        count++;
                    }
                }
                addEmptySpace();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void setTextToUser(String username, long socialPoints, long userSocialPoints, int index){
        String result = username;
        int division = 0;
        int otherUserDivion = 0;

        if(userSocialPoints <=  divisions[1])
            division = 1;

        else if(userSocialPoints <=  divisions[3])
            division = 2;

        else if(userSocialPoints <=  divisions[5])
            division = 3;

        else if(userSocialPoints <=  divisions[7])
            division = 4;

        if(socialPoints <=  divisions[1])
            otherUserDivion = 1;

        else if(socialPoints <=  divisions[3])
            otherUserDivion = 2;

        else if(socialPoints <=  divisions[5])
            otherUserDivion = 3;

        else if(socialPoints <=  divisions[7])
            otherUserDivion = 4;

        if(otherUserDivion - division == 0){
            result = result+"(Very good)";
        }
        else if(otherUserDivion - division == 1 || division - otherUserDivion == 1){
            result = result+"(good)";
        }
        else if(otherUserDivion - division == 2 || division - otherUserDivion == 2){
            result = result+"(bad)";
        }
        else if(otherUserDivion - division == 3 || division - otherUserDivion == 3){
            result = result+"(very bad)";
        }

        name.get(index).setText(result);
    }

    private void setBioToUser(String username, final int index){
        dbRef.child(UserInfo.USERS_ACCESS).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.BIO_ACCESS)){
                    String value = (String)dataSnapshot.child(UserInfo.BIO_ACCESS).getValue();
                    if(value.length() > 60)
                        value = value.substring(0,60);
                    bios.get(index).setText(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void setPhotoToUser(String username, final int index){
        dbRef.child(UserInfo.USERS_ACCESS).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                    Picasso.with(SelectPerson.this).load(String.valueOf(dataSnapshot.child(UserInfo.PROFILE_PICTURE_ACCESS).getValue())).fit().centerInside().into(profiles.get(index));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void setListenersToLayouts(final String otherUser, final int index){
        profiles.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectPerson.this, Profile.class);
                intent.putExtra("user", otherUser);
                startActivity(intent);
            }

        });

        talk.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectPerson.this, Message.class);
                intent.putExtra("user", otherUser);
                startActivity(intent);
            }

        });

    }

    private void addEmptySpace() {
        int dpPad56 = (int) UserInfo.convertDpToPixel(56, getApplicationContext());

        ImageButton commentBtn = new ImageButton(this);
        commentBtn.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams layImg2 = new LinearLayout.LayoutParams(dpPad56, dpPad56);
        commentBtn.setLayoutParams(layImg2);

        layoutMain.addView(commentBtn);
    }

    private void checkUserColor(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.LAYOUT_COLOR_ACCESS)){
                    setColorToTop((Long)dataSnapshot.child(UserInfo.LAYOUT_COLOR_ACCESS).getValue());
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
}
