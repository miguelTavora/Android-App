package dam_path.dam_45102.run;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

import dam_path.dam_45102.storage.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupRequest extends AppCompatActivity {

    private LinearLayout layoutMain;
    private String groupName;
    private DatabaseReference dbRef;
    private ArrayList<LinearLayout> request;
    private ArrayList<CircleImageView> userImg;
    private ArrayList<Button> acceptBtn;
    private ArrayList<Button> declineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_request);

        dbRef = FirebaseDatabase.getInstance().getReference();
        layoutMain = findViewById(R.id.layoutMain);
        request= new ArrayList<>();
        userImg= new ArrayList<>();
        acceptBtn = new ArrayList<>();
        declineBtn = new ArrayList<>();

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            groupName = extras.getString("name");

        }

        setRequestsWithDB();
        checkUserColor();


        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    private void createLayoutRequest(String nameUser){
        //principal
        LinearLayout lin = new LinearLayout(this);
        LinearLayout.LayoutParams layMatch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
        layMatch.setMargins(dpPad10,dpPad10,dpPad10,dpPad10);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setBackgroundResource(R.drawable.box_aroud);
        lin.setWeightSum(6f);
        lin.setLayoutParams(layMatch);

        //imagem perfil usuario
        CircleImageView photo = new CircleImageView(this);
        int dpPad55 = (int) UserInfo.convertDpToPixel(55, getApplicationContext());
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(0, dpPad55,1f);
        layParams.gravity = Gravity.CENTER;
        int dpPad2 = (int) UserInfo.convertDpToPixel(2, getApplicationContext());
        layParams.setMargins(dpPad10,dpPad2,0,dpPad2);
        photo.setImageResource(R.drawable.profile);
        photo.setLayoutParams(layParams);

        //layout com nome e os butoes
        LinearLayout layLin = new LinearLayout(this);
        LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,5f);
        layLin.setOrientation(LinearLayout.VERTICAL);
        layLin.setLayoutParams(layParams2);


        //texto nome user
        TextView name = new TextView(this);
        LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layParams3.gravity = Gravity.CENTER;
        layParams3.setMargins(0,0,0,dpPad10);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        name.setText(nameUser);
        name.setLayoutParams(layParams3);


        //layout dos butoes
        LinearLayout linLay2 = new LinearLayout(this);
        LinearLayout.LayoutParams layParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layParams4.gravity = LinearLayout.HORIZONTAL;
        linLay2.setWeightSum(6f);
        linLay2.setLayoutParams(layParams4);



        Button btnAccept = new Button(this);
        LinearLayout.LayoutParams layParams5 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,3);
        int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
        layParams5.setMargins(dpPad5,0,dpPad5,dpPad5);
        btnAccept.setMinimumHeight(0);
        btnAccept.setMinHeight(0);
        btnAccept.setText(R.string.accept);
        btnAccept.setLayoutParams(layParams5);



        Button btnDecline = new Button(this);
        btnDecline.setText(R.string.decline);
        btnDecline.setMinimumHeight(0);
        btnDecline.setMinHeight(0);
        btnDecline.setLayoutParams(layParams5);

        linLay2.addView(btnAccept);
        linLay2.addView(btnDecline);


        layLin.addView(name);
        layLin.addView(linLay2);

        lin.addView(photo);
        lin.addView(layLin);

        acceptBtn.add(btnAccept);
        declineBtn.add(btnDecline);
        userImg.add(photo);
        request.add(lin);
        layoutMain.addView(lin);

    }

    private void addListenerToButtons(final int index, final String username){
        acceptBtn.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.REQUEST_GROUP_ACCESS).child(username).removeValue();
                dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.MEMBERS_GROUPS_ACCESS).child(username).setValue(true);
                request.get(index).setVisibility(View.GONE);
            }

        });

        declineBtn.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.REQUEST_GROUP_ACCESS).child(username).removeValue();
                request.get(index).setVisibility(View.GONE);
            }

        });

    }

    private void setRequestsWithDB(){
        dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.REQUEST_GROUP_ACCESS)){
                    dbRef.child(UserInfo.GROUPS_ACCESS).child(groupName).child(UserInfo.REQUEST_GROUP_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                createLayoutRequest(snapshot.getKey());
                                addListenerToButtons(count,snapshot.getKey());
                                setProfilePhotoToMessage(snapshot.getKey(),count);
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

    private void setProfilePhotoToMessage(final String username, final int index){
        dbRef.child(UserInfo.USERS_ACCESS).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                    dbRef.child(UserInfo.USERS_ACCESS).child(username).child(UserInfo.PROFILE_PICTURE_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Picasso.with(GroupRequest.this).load(String.valueOf(dataSnapshot.getValue())).fit().centerInside().into(userImg.get(index));
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
