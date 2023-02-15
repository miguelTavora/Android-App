package dam_path.dam_45102.run;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Group extends AppCompatActivity {

    private ScrollView scrollGroups;
    private LinearLayout mainLayout;
    private ImageView profileBtn;
    private DatabaseReference dbRef;

    //variaveis criadas dentro do layout
    private ArrayList<LinearLayout> groupsLayout;
    private ArrayList<ImageView> groupsPhoto;
    private ArrayList<String> groupNameAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups);

        scrollGroups = findViewById(R.id.scrollGroups);
        mainLayout = findViewById(R.id.layoutMain);
        dbRef = FirebaseDatabase.getInstance().getReference();
        profileBtn = findViewById(R.id.profile);

        groupsLayout = new ArrayList<>();
        groupsPhoto = new ArrayList<>();
        groupNameAll = new ArrayList<>();

        setContentDB();
        setProfilePic();
        checkUserColor();


        ImageButton homeBtn = findViewById(R.id.home);
        ImageButton searchBtn = findViewById(R.id.search);
        ImageButton createBtn = findViewById(R.id.create);
        ImageButton rankBtn = findViewById(R.id.rank);
        final EditText searchName = findViewById(R.id.name);


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group.this, Content.class);
                startActivity(intent);
            }

        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollGroups.fullScroll(ScrollView.FOCUS_UP);
            }

        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group.this, CreateGroup.class);
                startActivity(intent);
            }

        });

        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group.this, Content.class);
                intent.putExtra("rank", true);
                startActivity(intent);

            }

        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group.this, Profile.class);
                startActivity(intent);
            }

        });

        searchName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                String txt = searchName.getText().toString();
                if(txt.length() > 0) {
                    for(int i = 0; i< groupNameAll.size();i++){
                        if(!groupNameAll.get(i).startsWith(txt))
                            groupsLayout.get(i).setVisibility(View.GONE);
                        else
                            groupsLayout.get(i).setVisibility(View.VISIBLE);
                    }
                }else{
                    for(int i = 0; i< groupNameAll.size();i++){
                        groupsLayout.get(i).setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void setLayoutGroups(String groupNameDB, String numMembers,String descriptionDB){
        //linear layout principal
        LinearLayout lin = new LinearLayout(this);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dp10 = (int)UserInfo.convertDpToPixel(10,this);
        layParams.setMargins(dp10,dp10,dp10,dp10);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setBackgroundResource(R.drawable.box_aroud);
        lin.setWeightSum(5f);
        lin.setLayoutParams(layParams);

        //foto grupo
        CircleImageView photo = new CircleImageView(this);
        int dp60 = (int)UserInfo.convertDpToPixel(60,this);
        int dp2 = (int)UserInfo.convertDpToPixel(2,this);
        LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(0, dp60,1f);
        layParams2.setMargins(0,dp2,0,dp2);
        layParams2.gravity = Gravity.CENTER;
        photo.setImageResource(R.drawable.profile);
        photo.setLayoutParams(layParams2);


        //layout vertical
        LinearLayout linInside = new LinearLayout(this);
        LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,4f);
        linInside.setOrientation(LinearLayout.VERTICAL);
        linInside.setLayoutParams(layParams3);

        //layout horizontal
        LinearLayout linNameMembers = new LinearLayout(this);
        LinearLayout.LayoutParams layParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,4f);
        int dp3 = (int)UserInfo.convertDpToPixel(3,this);
        layParams4.setMargins(0,dp3,0,0);
        linNameMembers.setWeightSum(2f);
        linNameMembers.setOrientation(LinearLayout.HORIZONTAL);
        linNameMembers.setLayoutParams(layParams4);

        //Texto nome grupo
        TextView groupName = new TextView(this);
        LinearLayout.LayoutParams layParams5 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        layParams5.gravity = Gravity.CENTER;
        groupName.setGravity(Gravity.CENTER);
        groupName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        groupName.setText(groupNameDB);
        groupName.setLayoutParams(layParams5);

        //Texto numero membros
        TextView numberMembers = new TextView(this);
        numberMembers.setGravity(Gravity.CENTER);
        numberMembers.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        numberMembers.setText("Members: "+numMembers);
        numberMembers.setLayoutParams(layParams5);

        linNameMembers.addView(groupName);
        linNameMembers.addView(numberMembers);


        //Texto descrição grupo
        TextView description = new TextView(this);
        LinearLayout.LayoutParams layParams6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        layParams6.gravity = Gravity.CENTER;
        description.setGravity(Gravity.START);
        description.setPadding(dp3,0,0,0);
        description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        InputFilter[] fArray = new InputFilter[]{new InputFilter.LengthFilter(20)};
        description.setFilters(fArray);
        description.setText(descriptionDB);
        description.setLayoutParams(layParams6);

        linInside.addView(linNameMembers);
        linInside.addView(description);

        lin.addView(photo);
        lin.addView(linInside);


        groupNameAll.add(groupNameDB);
        groupsPhoto.add(photo);
        groupsLayout.add(lin);
        mainLayout.addView(lin);
    }

    private void addEmptySpace() {
        int dpPad56 = (int) UserInfo.convertDpToPixel(56, getApplicationContext());

        ImageButton commentBtn = new ImageButton(this);
        commentBtn.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams layImg2 = new LinearLayout.LayoutParams(dpPad56, dpPad56);
        commentBtn.setLayoutParams(layImg2);

        mainLayout.addView(commentBtn);
    }

    private void setContentDB(){
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.GROUPS_ACCESS)) {
                    dbRef.child(UserInfo.GROUPS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                setLayoutGroups(snapshot.getKey(),""+snapshot.child(UserInfo.MEMBERS_GROUPS_ACCESS).getChildrenCount(),""+snapshot.child(UserInfo.DESCRIPTION_GROUPS_ACCESS).getValue());
                                Picasso.with(Group.this).load( String.valueOf(snapshot.child(UserInfo.IMAGE_GROUPS_ACCESS).getValue())).fit().centerInside().into(groupsPhoto.get(count));
                                setListenerToLayout(snapshot.getKey(),count);
                                count++;
                            }
                            addEmptySpace();
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

    private void setListenerToLayout(final String groupName, final int indexList){
        groupsLayout.get(indexList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.MEMBERS_GROUPS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean insideGroup = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(Objects.equals(snapshot.getKey(), UserInfo.username)){
                                insideGroup = true;
                                break;
                            }
                        }
                        if(insideGroup){
                            Intent intent = new Intent(Group.this, GroupChat.class);
                            intent.putExtra("name",groupName);
                            startActivity(intent);
                        }else{
                            dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.REQUEST_GROUP_ACCESS).child(UserInfo.username).setValue(true);
                            Toast.makeText(Group.this, "Request sent with success", Toast.LENGTH_LONG).show();
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

    private void setProfilePic(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                    dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.PROFILE_PICTURE_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Picasso.with(Group.this).load(String.valueOf(dataSnapshot.getValue())).fit().centerInside().into(profileBtn);
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
                    setColorToBottom((Long)dataSnapshot.child(UserInfo.LAYOUT_COLOR_ACCESS).getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
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
