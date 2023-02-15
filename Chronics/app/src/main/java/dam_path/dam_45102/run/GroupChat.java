package dam_path.dam_45102.run;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class GroupChat extends AppCompatActivity {

    private ScrollView scroll;
    private String groupName;
    private EditText message;
    private DatabaseReference dbRef;
    private LinearLayout layoutMain;
    private ArrayList<ImageButton> emptySpace;
    private ArrayList<ImageView> otherProfile;
    private boolean flagHasCotent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat);

        scroll = findViewById(R.id.scroll);
        message = findViewById(R.id.message);
        dbRef = FirebaseDatabase.getInstance().getReference();
        layoutMain = findViewById(R.id.layoutMain);
        emptySpace = new ArrayList<>();
        otherProfile = new ArrayList<>();
        flagHasCotent = false;

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            groupName = extras.getString("name");

        }

        isAdmin();
        createMessagesWithBD();
        checkUserColor();

        ImageButton backBtn = findViewById(R.id.back);

        Button send = findViewById(R.id.send);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.MESSAGES_GROUP_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String msn = message.getText().toString();
                        if(!msn.equals("")) {
                            dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.MESSAGES_GROUP_ACCESS).child("" + dataSnapshot.getChildrenCount()).child(UserInfo.USER_GROUP_ACCESS)
                                    .setValue(UserInfo.username);
                            dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.MESSAGES_GROUP_ACCESS).child("" + dataSnapshot.getChildrenCount()).child(UserInfo.MESSAGE_GROUP_ACCESS)
                                    .setValue(msn);

                            if (flagHasCotent) {
                                emptySpace.get(0).setVisibility(View.GONE);
                                emptySpace.remove(0);
                            }
                            createUserMessage(msn);
                            createEmptySpace();
                            flagHasCotent = true;
                            message.setText(null);
                            scroll.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN);
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

        });


    }

    //se for o administrador aparece o icon para aceitar pessoas senao nao
    private void isAdmin(){
        dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.ADMIN_GROUPS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String admin = (String)dataSnapshot.getValue();
                assert admin != null;
                if(!admin.equals(UserInfo.username)){
                    ImageButton scheduleBtn = findViewById(R.id.schedule);
                    scheduleBtn.setVisibility(View.GONE);
                }else{
                    ImageButton scheduleBtn = findViewById(R.id.schedule);
                    scheduleBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(GroupChat.this, GroupRequest.class);
                            intent.putExtra("name",groupName);
                            startActivity(intent);
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

    private void createOtherPeopleLayout(String message){
        LinearLayout lin = new LinearLayout(this);
        LinearLayout.LayoutParams layMatch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
        layMatch.setMargins(0,dpPad10,0,dpPad10);
        lin.setWeightSum(10f);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setLayoutParams(layMatch);

        CircleImageView profilePic = new CircleImageView(this);
        int dpPad40 = (int) UserInfo.convertDpToPixel(40, getApplicationContext());
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(0, dpPad40,1f);
        layParams.setMargins(dpPad10,0,0,0);
        profilePic.setImageResource(R.drawable.profile);
        layParams.gravity = Gravity.BOTTOM;
        profilePic.setLayoutParams(layParams);

        LinearLayout linLay = new LinearLayout(this);
        LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,6f);
        int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
        layParams2.setMargins(dpPad5,dpPad5,dpPad5,dpPad5);
        linLay.setOrientation(LinearLayout.HORIZONTAL);

        linLay.setLayoutParams(layParams2);

        TextView msn = new TextView(this);
        LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dpPad20 = (int) UserInfo.convertDpToPixel(20, getApplicationContext());
        msn.setPadding(dpPad20,dpPad5,dpPad10,dpPad5);
        msn.setText(message);
        msn.setBackgroundResource(R.drawable.bubble_talk_round);
        msn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        msn.setLayoutParams(layParams3);

        linLay.addView(msn);


        lin.addView(profilePic);
        lin.addView(linLay);
        otherProfile.add(profilePic);

        layoutMain.addView(lin);
    }

    private void createEmptySpace(){
        ImageButton btn = new ImageButton(this);
        int dpPad55 = (int) UserInfo.convertDpToPixel(55, getApplicationContext());
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpPad55);
        btn.setBackgroundResource(0);
        btn.setLayoutParams(layParams);

        layoutMain.addView(btn);
        emptySpace.add(btn);
    }

    private void createUserMessage(String message){
        //layout principal
        LinearLayout lin = new LinearLayout(this);
        LinearLayout.LayoutParams layMatch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
        layMatch.setMargins(0,dpPad10,0,dpPad10);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setLayoutParams(layMatch);

        //secundario
        LinearLayout linLay = new LinearLayout(this);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        layParams.gravity = Gravity.END;
        linLay.setWeightSum(10f);
        linLay.setOrientation(LinearLayout.HORIZONTAL);
        linLay.setLayoutParams(layParams);

        //terceiro vazio
        LinearLayout linLayX = new LinearLayout(this);
        LinearLayout.LayoutParams layParamsX = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,4f);
        linLayX.setOrientation(LinearLayout.HORIZONTAL);
        linLayX.setLayoutParams(layParamsX);


        //terceiro
        LinearLayout linLay2 = new LinearLayout(this);
        LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,6f);
        int dpPad8 = (int) UserInfo.convertDpToPixel(8, getApplicationContext());
        int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
        int dpPad22 = (int) UserInfo.convertDpToPixel(22, getApplicationContext());
        layParams3.setMargins(dpPad8,dpPad5,dpPad22,dpPad5);
        linLay2.setOrientation(LinearLayout.HORIZONTAL);
        linLay2.setLayoutParams(layParams3);


        LinearLayout linLay3 = new LinearLayout(this);
        LinearLayout.LayoutParams layParams4 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        linLay3.setOrientation(LinearLayout.VERTICAL);
        linLay3.setLayoutParams(layParams4);


        TextView msn = new TextView(this);
        LinearLayout.LayoutParams layParams5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        msn.setBackgroundResource(R.drawable.bubble_talk_round_outcome);
        int dpPad20 = (int) UserInfo.convertDpToPixel(20, getApplicationContext());
        msn.setPadding(dpPad10,dpPad5,dpPad20,dpPad5);
        layParams5.gravity = Gravity.END;
        msn.setText(message);
        msn.setLayoutParams(layParams5);


        linLay3.addView(msn);
        linLay2.addView(linLay3);
        linLay.addView(linLayX);
        linLay.addView(linLay2);
        lin.addView(linLay);

        layoutMain.addView(lin);
    }

    private void createMessagesWithBD(){
        dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.MESSAGES_GROUP_ACCESS)){
                    dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.MESSAGES_GROUP_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int counter = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(Objects.equals(snapshot.child(UserInfo.USER_GROUP_ACCESS).getValue(), UserInfo.username))
                                    createUserMessage((String)snapshot.child(UserInfo.MESSAGE_GROUP_ACCESS).getValue());
                                else{
                                    createOtherPeopleLayout((String)snapshot.child(UserInfo.MESSAGE_GROUP_ACCESS).getValue());
                                    setProfilePhotoToMessage((String)snapshot.child(UserInfo.USER_GROUP_ACCESS).getValue(),counter);
                                    counter++;
                                }
                                flagHasCotent = true;
                            }
                            if(flagHasCotent)
                                createEmptySpace();
                            scroll.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll.fullScroll(View.FOCUS_DOWN);
                                }
                            });
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

    private void setProfilePhotoToMessage(final String username, final int index){
        dbRef.child(UserInfo.USERS_ACCESS).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                    dbRef.child(UserInfo.USERS_ACCESS).child(username).child(UserInfo.PROFILE_PICTURE_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Picasso.with(GroupChat.this).load(String.valueOf(dataSnapshot.getValue())).fit().centerInside().into(otherProfile.get(index));
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
