package dam_path.dam_45102.run;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import dam_path.dam_45102.storage.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class Conversations extends AppCompatActivity {

    private DatabaseReference dbRef;
    private LinearLayout layoutMain;
    private ArrayList<LinearLayout> layUsers;
    private ArrayList<CircleImageView> profiles;
    private ArrayList<String> usersName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversations);

        dbRef = FirebaseDatabase.getInstance().getReference();
        layoutMain = findViewById(R.id.layoutMain);
        layUsers = new ArrayList<>();
        profiles= new ArrayList<>();
        usersName= new ArrayList<>();

        ImageButton backBtn = findViewById(R.id.back);
        ImageButton meetBtn = findViewById(R.id.meet);
        ImageButton suicideBtn = findViewById(R.id.suicide);
        final EditText searchName = findViewById(R.id.name);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Conversations.this, Content.class);
                startActivity(intent);
            }

        });

        createLayoutWithBD();
        checkUserColor();


        meetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnsweredQuestionnaire();
            }

        });


        suicideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Conversations.this, Suicide.class);
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
                    for(int i = 0; i< usersName.size();i++){
                        if(!usersName.get(i).startsWith(txt))
                            layUsers.get(i).setVisibility(View.GONE);
                        else
                            layUsers.get(i).setVisibility(View.VISIBLE);
                    }
                }else{
                    for(int i = 0; i< usersName.size();i++){
                        layUsers.get(i).setVisibility(View.VISIBLE);
                    }
                }
            }
        });


    }

    private void checkAnsweredQuestionnaire(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.SOCIAL_POINTS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((Long)dataSnapshot.getValue() == 0){
                    Intent intent = new Intent(Conversations.this, Questionnaire.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(Conversations.this, SelectPerson.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void createLayoutWithBD(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                if(dataSnapshot.hasChild(UserInfo.USER_CONVERSATIONS_ACCESS)){
                    for (DataSnapshot snapshot : dataSnapshot.child(UserInfo.USER_CONVERSATIONS_ACCESS).getChildren()) {
                        if(Objects.equals(snapshot.child((snapshot.getChildrenCount() - 1) + "").child(UserInfo.USER_GROUP_ACCESS).getValue(), UserInfo.username)) {
                            createLayout(snapshot.getKey(), "You: " + snapshot.child((snapshot.getChildrenCount() - 1) + "").child(UserInfo.MESSAGE_GROUP_ACCESS).getValue());
                            setListenerToLayouts(snapshot.getKey(),count);
                            setPhotoToUser(snapshot.getKey(),count);
                        }
                        else {
                            createLayout(snapshot.getKey(), "" + snapshot.child((snapshot.getChildrenCount() - 1) + "").child(UserInfo.MESSAGE_GROUP_ACCESS).getValue());
                            setListenerToLayouts(snapshot.getKey(),count);
                            setPhotoToUser(snapshot.getKey(),count);
                        }
                        count++;
                    }
                    addEmptySpace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void createLayout(String userX, String lastMsn){
        LinearLayout lin = new LinearLayout(this);
        LinearLayout.LayoutParams layParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
        int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
        layParam.setMargins(dpPad10,dpPad5,dpPad10,dpPad10);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setPadding(0,0,0,dpPad10);
        lin.setWeightSum(6);
        lin.setBackgroundResource(R.drawable.text_lines);
        lin.setLayoutParams(layParam);


        CircleImageView img = new CircleImageView(this);
        int dpPad55 = (int) UserInfo.convertDpToPixel(55, getApplicationContext());
        LinearLayout.LayoutParams layParam2 = new LinearLayout.LayoutParams(0, dpPad55,1);
        int dpPad2 = (int) UserInfo.convertDpToPixel(2, getApplicationContext());
        layParam2.setMargins(0,dpPad2,dpPad10,dpPad2);
        layParam2.gravity = Gravity.CENTER;
        img.setImageResource(R.drawable.profile);
        img.setLayoutParams(layParam2);



        LinearLayout linLay = new LinearLayout(this);
        LinearLayout.LayoutParams layParam3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,5);
        linLay.setOrientation(LinearLayout.VERTICAL);
        linLay.setLayoutParams(layParam3);


        TextView name = new TextView(this);
        LinearLayout.LayoutParams layParam4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layParam4.gravity = Gravity.CENTER;
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        name.setText(userX);
        name.setLayoutParams(layParam4);

        TextView msn = new TextView(this);
        msn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        if(lastMsn.length() > 40)
            lastMsn = lastMsn.substring(0,40);
        msn.setText(lastMsn);
        msn.setLayoutParams(layParam4);

        linLay.addView(name);
        linLay.addView(msn);

        lin.addView(img);
        lin.addView(linLay);

        usersName.add(userX);
        layUsers.add(lin);
        profiles.add(img);
        layoutMain.addView(lin);
    }

    private void addEmptySpace() {
        int dpPad56 = (int) UserInfo.convertDpToPixel(56, getApplicationContext());

        ImageButton commentBtn = new ImageButton(this);
        commentBtn.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams layImg2 = new LinearLayout.LayoutParams(dpPad56, dpPad56);
        commentBtn.setLayoutParams(layImg2);

        layoutMain.addView(commentBtn);
    }

    private void setListenerToLayouts(final String otherUser, int index){
        layUsers.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Conversations.this, Message.class);
                intent.putExtra("user", otherUser);
                startActivity(intent);
            }

        });
    }

    private void setPhotoToUser(String username, final int index){
        dbRef.child(UserInfo.USERS_ACCESS).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                    Picasso.with(Conversations.this).load(String.valueOf(dataSnapshot.child(UserInfo.PROFILE_PICTURE_ACCESS).getValue())).fit().centerInside().into(profiles.get(index));
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
