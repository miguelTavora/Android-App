package dam_path.dam_45102.run;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dam_path.dam_45102.storage.UserInfo;

public class LayoutChange extends AppCompatActivity {

    private DatabaseReference dbRef;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_changer);

        state = -1;

        final Animation rotate = AnimationUtils.loadAnimation(this,R.anim.rotation);
        final Animation swipe = AnimationUtils.loadAnimation(this,R.anim.slide_right);
        final Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
        final Animation bubble = AnimationUtils.loadAnimation(this,R.anim.bubble);


        dbRef = FirebaseDatabase.getInstance().getReference();

        ImageButton backBtn = findViewById(R.id.back);
        Button whiteBtn = findViewById(R.id.white);
        Button blackBtn = findViewById(R.id.black);
        Button redBtn = findViewById(R.id.red);
        Button blueBtn = findViewById(R.id.blue);

        checkUserColor();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state != -1) {
                    Intent intent = new Intent(LayoutChange.this, EditProfile.class);
                    intent.putExtra("color", state);
                    startActivity(intent);
                }else
                    finish();
            }

        });

        whiteBtn.setOnClickListener(new View.OnClickListener() {//TODO
            @Override
            public void onClick(View v) {
                v.startAnimation(rotate);
                setLocalColorToTop(0);
                dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.LAYOUT_COLOR_ACCESS).setValue(0);
                state = 0;
            }

        });

        blackBtn.setOnClickListener(new View.OnClickListener() {//TODO
            @Override
            public void onClick(View v) {
                v.startAnimation(swipe);
                setLocalColorToTop(1);
                dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.LAYOUT_COLOR_ACCESS).setValue(1);
                state = 1;
            }

        });

        redBtn.setOnClickListener(new View.OnClickListener() {//TODO
            @Override
            public void onClick(View v) {
                v.startAnimation(bubble);
                setLocalColorToTop(2);
                dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.LAYOUT_COLOR_ACCESS).setValue(2);
                state = 2;
            }

        });

        blueBtn.setOnClickListener(new View.OnClickListener() {//TODO
            @Override
            public void onClick(View v) {
                v.startAnimation(shake);
                setLocalColorToTop(3);
                dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.LAYOUT_COLOR_ACCESS).setValue(3);
                state = 3;
            }

        });
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

    private void setLocalColorToTop(long color){
        LinearLayout lin = findViewById(R.id.topBar);
        LinearLayout lin2 = findViewById(R.id.topBar2);

        if(color == 0){
            lin.setBackgroundResource(R.color.white);
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
