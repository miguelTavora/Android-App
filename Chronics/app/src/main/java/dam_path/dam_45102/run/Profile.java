package dam_path.dam_45102.run;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import dam_path.dam_45102.storage.UserInfo;

public class Profile extends AppCompatActivity {

    private DatabaseReference dbRef;
    private long numStorys;
    private long numLikes;
    private LinearLayout layoutMain;
    private LinearLayout layoutContent;
    private ImageView img;
    private TextView userinfo;
    private TextView userBio;
    private ArrayList<TextView>  dataTxt;
    private ArrayList<TextView>  storyTxt;
    private ArrayList<TextView>  likesTxt;
    private ArrayList<ImageButton>  btnsLike;

    private String otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        dbRef = FirebaseDatabase.getInstance().getReference();
        numStorys = 0;
        numLikes = 0;
        img = findViewById(R.id.profileImg);
        userinfo = findViewById(R.id.userInfo);
        userBio = findViewById(R.id.bio);
        dataTxt = new ArrayList<>();
        storyTxt = new ArrayList<>();
        likesTxt = new ArrayList<>();
        btnsLike = new ArrayList<>();
        layoutMain = findViewById(R.id.layoutMain);
        layoutContent = findViewById(R.id.principalLayout);
        otherUser = null;

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            otherUser = extras.getString("user");

        }

        ImageButton logoutBtn = findViewById(R.id.logout);

        if(otherUser == null){
            TextView editText = findViewById(R.id.edit);

            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Profile.this, EditProfile.class);
                    startActivity(intent);
                }

            });

            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Profile.this, MainActivity.class);
                    startActivity(intent);
                }

            });
        }else{
            TextView editText = findViewById(R.id.edit);
            editText.setVisibility(View.GONE);
            logoutBtn.setImageResource(R.drawable.return_icon);

            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }

            });
        }


        getProfileContent();
        getUserBio();
        setStoryToProfile();
        setProfilePic();
        checkUserColor();


        ImageButton homeBtn = findViewById(R.id.home);
        ImageButton searchBtn = findViewById(R.id.search);
        ImageButton addBtn = findViewById(R.id.add);
        ImageButton rankBtn = findViewById(R.id.rank);




        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Content.class);
                startActivity(intent);
            }

        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Group.class);
                startActivity(intent);
            }

        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, ContentAdd.class);
                startActivity(intent);
            }

        });

        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Content.class);
                intent.putExtra("rank", true);
                startActivity(intent);
            }

        });
    }

    private void getProfileContent (){
        dbRef.child(UserInfo.PUBLICATIONS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (otherUser == null) {
                    if (dataSnapshot.hasChild(UserInfo.username)) {
                        dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                numStorys = dataSnapshot.getChildrenCount();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    numLikes += (Long) snapshot.child(UserInfo.LIKES_ACCESS).getValue();
                                }
                                userinfo.setText(UserInfo.username + "\nNº of likes: " + numLikes + "\nNº of story: " + numStorys);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("cancel", "onCancelled", error.toException());
                            }
                        });
                    } else
                        userinfo.setText(UserInfo.username + "\nNº of likes: " + numLikes + "\nNº of story: " + numStorys);
                }else{
                    if (dataSnapshot.hasChild(otherUser)) {
                        dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(otherUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                numStorys = dataSnapshot.getChildrenCount();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    numLikes += (Long) snapshot.child(UserInfo.LIKES_ACCESS).getValue();
                                }
                                userinfo.setText(otherUser + "\nNº of likes: " + numLikes + "\nNº of story: " + numStorys);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("cancel", "onCancelled", error.toException());
                            }
                        });
                    } else
                        userinfo.setText(otherUser + "\nNº of likes: " + numLikes + "\nNº of story: " + numStorys);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void getUserBio(){
        dbRef.child(UserInfo.USERS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(otherUser == null) {
                    if (dataSnapshot.child(UserInfo.username).hasChild(UserInfo.BIO_ACCESS)) {
                        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.BIO_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userBio.setText((String) dataSnapshot.getValue());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("cancel", "onCancelled", error.toException());
                            }
                        });
                    } else
                        userBio.setText("");
                }else{
                    if (dataSnapshot.child(otherUser).hasChild(UserInfo.BIO_ACCESS)) {
                        dbRef.child(UserInfo.USERS_ACCESS).child(otherUser).child(UserInfo.BIO_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userBio.setText((String) dataSnapshot.getValue());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("cancel", "onCancelled", error.toException());
                            }
                        });
                    } else
                        userBio.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void createStorysLayout(){
        LinearLayout lin = new LinearLayout(this);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
        layParams.setMargins(0,dpPad10,0,dpPad10);
        lin.setBackgroundResource(R.drawable.box_aroud);
        lin.setOrientation(LinearLayout.VERTICAL);
        lin.setLayoutParams(layParams);


        //para data
        LinearLayout lin2 = new LinearLayout(this);
        LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lin2.setBackgroundResource(R.drawable.text_lines);
        lin2.setOrientation(LinearLayout.HORIZONTAL);
        lin2.setLayoutParams(layParams2);

        TextView data = new TextView(this);
        LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
        int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
        layParams3.setMargins(0,0,dpPad5,0);
        int dpPad3 = (int) UserInfo.convertDpToPixel(3, getApplicationContext());
        data.setPadding(dpPad3,dpPad3,0,dpPad3);
        data.setGravity(Gravity.END);
        data.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        data.setLayoutParams(layParams3);
        lin2.addView(data);
        dataTxt.add(data);


        //para story
        LinearLayout lin3 = new LinearLayout(this);
        lin3.setBackgroundResource(R.drawable.text_lines);
        lin3.setOrientation(LinearLayout.HORIZONTAL);
        lin3.setLayoutParams(layParams2);

        TextView story = new TextView(this);
        LinearLayout.LayoutParams layParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        story.setPadding(dpPad5,dpPad10,dpPad5,dpPad10);
        story.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        story.setLayoutParams(layParams4);
        lin3.addView(story);
        storyTxt.add(story);

        //numero likes
        LinearLayout lin4 = new LinearLayout(this);
        lin4.setBackgroundResource(R.drawable.text_lines);
        lin4.setOrientation(LinearLayout.HORIZONTAL);
        lin4.setLayoutParams(layParams2);

        TextView likes = new TextView(this);
        LinearLayout.LayoutParams layParams5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        likes.setGravity(Gravity.CENTER_VERTICAL);
        likes.setPadding(dpPad10,dpPad5,0,dpPad5);
        likes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        likes.setLayoutParams(layParams5);
        lin4.addView(likes);
        likesTxt.add(likes);


        //butão like
        ImageButton likeBtn = new ImageButton(this);
        likeBtn.setBackgroundColor(Color.TRANSPARENT);
        likeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        likeBtn.setBackgroundResource(R.drawable.like);
        LinearLayout lay = new LinearLayout(this);
        int dpPad38 = (int) UserInfo.convertDpToPixel(38, getApplicationContext());
        int dpPad32 = (int) UserInfo.convertDpToPixel(32, getApplicationContext());
        LinearLayout.LayoutParams layAround = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layAround.setMargins(dpPad5,0,0,0);
        lay.setLayoutParams(layAround);
        LinearLayout.LayoutParams layImg = new LinearLayout.LayoutParams(dpPad38, dpPad32);
        layImg.setMargins(dpPad3, dpPad5, 0, dpPad5);
        likeBtn.setLayoutParams(layImg);
        lay.addView(likeBtn);
        btnsLike.add(likeBtn);

        lin.addView(lin2);
        lin.addView(lin3);
        lin.addView(lin4);
        lin.addView(lay);
        layoutContent.addView(lin);
    }

    private void createEmptySpace(){
        ImageButton btn = new ImageButton(this);
        int dpPad59 = (int) UserInfo.convertDpToPixel(59, getApplicationContext());
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpPad59);
        btn.setBackgroundResource(0);
        btn.setLayoutParams(layParams);

        layoutMain.addView(btn);
    }

    private void setProfilePic(){
        dbRef.child(UserInfo.USERS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(otherUser == null) {
                    if(dataSnapshot.child(UserInfo.username).hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.PROFILE_PICTURE_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Picasso.with(Profile.this).load(String.valueOf(dataSnapshot.getValue())).fit().centerInside().into(img);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("cancel", "onCancelled", error.toException());
                            }
                        });
                    }
                }else{
                    if(dataSnapshot.child(otherUser).hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                        dbRef.child(UserInfo.USERS_ACCESS).child(otherUser).child(UserInfo.PROFILE_PICTURE_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Picasso.with(Profile.this).load(String.valueOf(dataSnapshot.getValue())).fit().centerInside().into(img);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("cancel", "onCancelled", error.toException());
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setTextToTextViews(String date, String story, Long likes, int index){
        dataTxt.get(index).setText(date);
        storyTxt.get(index).setText(story);
        likesTxt.get(index).setText(""+likes);
    }

    private void setStoryToProfile(){
        dbRef.child(UserInfo.PUBLICATIONS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(otherUser == null) {
                    if (dataSnapshot.hasChild(UserInfo.username)) {
                        dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int count = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    createStorysLayout();
                                    setTextToTextViews((String) snapshot.child(UserInfo.DATE_ACCESS).getValue(), (String) snapshot.child(UserInfo.STORY_ACCESS).getValue(),
                                            (Long) snapshot.child(UserInfo.LIKES_ACCESS).getValue(), count);
                                    addListenerToLikes(UserInfo.username, snapshot.getKey(), count);
                                    count++;
                                }
                                createEmptySpace();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("cancel", "onCancelled", error.toException());
                            }
                        });
                    }
                }else{
                    if (dataSnapshot.hasChild(otherUser)) {
                        dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(otherUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int count = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    createStorysLayout();
                                    setTextToTextViews((String) snapshot.child(UserInfo.DATE_ACCESS).getValue(), (String) snapshot.child(UserInfo.STORY_ACCESS).getValue(),
                                            (Long) snapshot.child(UserInfo.LIKES_ACCESS).getValue(), count);
                                    addListenerToLikes(otherUser, snapshot.getKey(), count);
                                    count++;
                                }
                                createEmptySpace();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("cancel", "onCancelled", error.toException());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void addListenerToLikes(final String username, final String index, final int listIndex){
        btnsLike.get(listIndex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(UserInfo.USER_LIKES_ACCESS)) {
                            boolean flag = false;
                            for (DataSnapshot snapshot : dataSnapshot.child(UserInfo.USER_LIKES_ACCESS).getChildren()) {
                                if (Objects.equals(snapshot.getKey(), UserInfo.username)) {
                                    flag = true;
                                    break;
                                }

                            }
                            if (!flag) {
                                Long likes = (Long) dataSnapshot.child(UserInfo.LIKES_ACCESS).getValue() + 1;
                                dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.LIKES_ACCESS).setValue(likes);
                                dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.USER_LIKES_ACCESS).child(UserInfo.username).setValue(true);
                                likesTxt.get(listIndex).setText("" + likes);
                            }
                        } else {
                            Long likes = (Long) dataSnapshot.child(UserInfo.LIKES_ACCESS).getValue() + 1;
                            dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.LIKES_ACCESS).setValue(likes);
                            dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.USER_LIKES_ACCESS).child(UserInfo.username).setValue(true);
                            likesTxt.get(listIndex).setText("" + likes);
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
