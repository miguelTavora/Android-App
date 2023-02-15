package dam_path.dam_45102.run;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import dam_path.dam_45102.storage.UserInfo;

public class Questionnaire extends AppCompatActivity {

    private DatabaseReference dbRef;
    private int state;
    private TextView numQuest;
    private TextView quest;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private int countPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire);

        dbRef = FirebaseDatabase.getInstance().getReference();
        countPoints = 0;
        state = 1;

        ImageButton backBtn = findViewById(R.id.back);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        numQuest = findViewById(R.id.numQuest);
        quest = findViewById(R.id.quest);

        stateQuestionnaire();
        checkUserColor();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInteraction();
                increaseSocialPoints(1);
            }

        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInteraction();
                increaseSocialPoints(2);
            }

        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInteraction();
                increaseSocialPoints(3);
            }

        });
    }

    private void userInteraction(){
        if(state != 10) {
            state++;
            stateQuestionnaire();
        }else{
            dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.SOCIAL_POINTS_ACCESS).setValue(countPoints);
            Intent intent = new Intent(Questionnaire.this, SelectPerson.class);
            startActivity(intent);
        }
    }

    private void increaseSocialPoints(int btn){
        switch(state){
            case 1:
            case 2:
            case 7:
            case 9:
                if(btn ==1)
                    countPoints+= 1;
                else if(btn == 2)
                    countPoints+= 2;
                else if(btn==3)
                    countPoints+= 3;
                break;
            case 3:
            case 5:
                if(btn ==1)
                    countPoints+= 3;
                else if(btn == 2)
                    countPoints+= 1;
                else if(btn==3)
                    countPoints+= 2;
                break;
            case 4:
            case 6:
            case 8:
                if(btn ==1)
                    countPoints+= 1;
                else if(btn == 2)
                    countPoints+= 3;
                else if(btn==3)
                    countPoints+= 2;
                break;
            case 10:
                if(btn ==1)
                    countPoints+= 3;
                else if(btn == 2)
                    countPoints+= 2;
                else if(btn==3)
                    countPoints+= 1;
                break;
        }
    }


    @SuppressLint({"SetTextI18n", "ResourceType"})
    private void stateQuestionnaire(){
        String qText = "Question ";
        numQuest.setText(qText +state);

        switch(state){
            case 1:
                quest.setText(R.string.quest1);
                btn1.setText(R.string.op11);
                btn2.setText(R.string.op12);
                btn3.setText(R.string.op13);
                break;
            case 2:
                quest.setText(R.string.quest2);
                btn1.setText(R.string.op21);
                btn2.setText(R.string.op22);
                btn3.setText(R.string.op23);
                break;
            case 3:
                quest.setText(R.string.quest3);
                btn1.setText(R.string.op31);
                btn2.setText(R.string.op32);
                btn3.setText(R.string.op33);
                break;
            case 4:
                quest.setText(R.string.quest4);
                btn1.setText(R.string.op41);
                btn2.setText(R.string.op42);
                btn3.setText(R.string.op43);
                break;
            case 5:
                quest.setText(R.string.quest5);
                btn1.setText(R.string.op51);
                btn2.setText(R.string.op52);
                btn3.setText(R.string.op53);
                break;
            case 6:
                quest.setText(R.string.quest6);
                btn1.setText(R.string.op61);
                btn2.setText(R.string.op62);
                btn3.setText(R.string.op63);
                break;
            case 7:
                quest.setText(R.string.quest7);
                btn1.setText(R.string.op71);
                btn2.setText(R.string.op72);
                btn3.setText(R.string.op73);
                break;
            case 8:
                quest.setText(R.string.quest8);
                btn1.setText(R.string.op81);
                btn2.setText(R.string.op82);
                btn3.setText(R.string.op83);
                break;
            case 9:
                quest.setText(R.string.quest9);
                btn1.setText(R.string.op91);
                btn2.setText(R.string.op92);
                btn3.setText(R.string.op93);
                break;
            case 10:
                quest.setText(R.string.quest10);
                btn1.setText(R.string.op101);
                btn2.setText(R.string.op102);
                btn3.setText(R.string.op103);
                break;
        }
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
