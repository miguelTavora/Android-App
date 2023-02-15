package dam_path.dam_45102.run;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dam_path.dam_45102.storage.UserInfo;


public class Content extends AppCompatActivity {

    private boolean flagRank;

    private ScrollView scrollContent;
    private LinearLayout linContent;
    private DatabaseReference dbRef;

    private ArrayList<String> usernamePost;
    private ArrayList<String> indexPost;
    private ArrayList<String> historyPost;
    private ArrayList<Long> likesPost;

    private ArrayList<LinearLayout> linConteudos;
    private ArrayList<LinearLayout> linIcons;
    private ArrayList<TextView> commentsTxt;
    private ArrayList<ImageButton> btnsLike;
    private ArrayList<ImageButton> btnsComments;
    private ArrayList<TextView> txtLikes;
    private ArrayList<Long> timerOrder;
    private Map<String, ArrayList<TextView>> txtComments;
    private Map<String, ArrayList<String>> commentsStored;//mapa com key = username+index, arraylist lin escondidos
    private Map<String, ArrayList<LinearLayout>> commentsPerPost;//mapa com key = username+index, arraylist lin escondidos


    private LinearLayout[] layoutWriteCommentFixed;
    private EditText[] commentWrote;
    private Button[] btnSubmitComment;

    private ImageView profileBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);

        flagRank = false;

        Bundle extras = getIntent().getExtras();
        ImageButton homeBtn = findViewById(R.id.home);
        ImageButton rankBtn = findViewById(R.id.rank);

        if(extras != null){
            flagRank = extras.getBoolean("rank");

            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Content.this, Content.class);
                    startActivity(intent);

                }

            });

            rankBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollContent.fullScroll(ScrollView.FOCUS_UP);

                }

            });


        }else{
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollContent.fullScroll(ScrollView.FOCUS_UP);
                }

            });

            rankBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Content.this, Content.class);
                    intent.putExtra("rank", true);
                    startActivity(intent);

                }

            });
        }

        scrollContent = findViewById(R.id.scrollContent);
        linContent = findViewById(R.id.linContent);
        dbRef = FirebaseDatabase.getInstance().getReference();

        usernamePost = new ArrayList<>();
        indexPost = new ArrayList<>();
        historyPost = new ArrayList<>();
        likesPost = new ArrayList<>();

        linConteudos = new ArrayList<>();
        commentsTxt = new ArrayList<>();
        linIcons = new ArrayList<>();
        btnsLike = new ArrayList<>();
        btnsComments = new ArrayList<>();
        txtLikes = new ArrayList<>();
        timerOrder = new ArrayList<>();
        txtComments = new HashMap<>();
        commentsStored = new HashMap<>();
        commentsPerPost = new HashMap<>();

        profileBtn = findViewById(R.id.profile);


        constructContentWithDB();
        setProfilePic();
        checkUserColor();

        ImageButton convesationsBtn = findViewById(R.id.conversations);
        ImageButton searchBtn = findViewById(R.id.search);
        ImageButton addBtn = findViewById(R.id.add);


        convesationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Content.this, Conversations.class);
                startActivity(intent);
            }

        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Content.this, Group.class);
                startActivity(intent);
            }

        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Content.this, ContentAdd.class);
                startActivity(intent);
            }

        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Content.this, Profile.class);
                startActivity(intent);
            }

        });
    }

    @SuppressLint("SetTextI18n")
    private void createMainLinearLayout(String username, String index, String story, Long numLikes) {
        //layout principal
        LinearLayout lin = new LinearLayout(this);
        lin.setBackgroundResource(R.drawable.box_aroud);
        lin.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layParams.setMargins(0, (int) UserInfo.convertDpToPixel(10f, getApplicationContext()), 0, (int) UserInfo.convertDpToPixel(10f, getApplicationContext()));

        lin.setLayoutParams(layParams);


        LinearLayout lin2 = getGenericLinearLayout();

        //mostrar username
        TextView user = new TextView(this);
        LinearLayout.LayoutParams layMatch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int dpPad3 = (int) UserInfo.convertDpToPixel(3, getApplicationContext());
        user.setPadding(dpPad3, dpPad3, 0, dpPad3);
        user.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        user.setText(username);
        user.setLayoutParams(layMatch);

        lin2.addView(user);


        //mostrar historia
        LinearLayout lin3 = getGenericLinearLayout();

        TextView history = new TextView(this);
        int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
        int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
        history.setPadding(dpPad5, dpPad10, dpPad5, dpPad10);
        history.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        history.setText(story);
        history.setLayoutParams(layMatch);

        lin3.addView(history);


        //mostrar likes e comentarios
        LinearLayout lin4 = getGenericLinearLayout();

        TextView comments = new TextView(this);
        LinearLayout.LayoutParams layWrap = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        comments.setGravity(Gravity.CENTER_VERTICAL);
        comments.setPadding(0, dpPad10, dpPad5, dpPad5);
        comments.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layWrap.gravity = Gravity.START;
        comments.setLayoutParams(layWrap);
        commentsTxt.add(comments);

        TextView likes = new TextView(this);
        LinearLayout.LayoutParams layComments = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        likes.setGravity(Gravity.CENTER_VERTICAL);
        likes.setPadding(dpPad5, dpPad10, 0, dpPad5);
        likes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        likes.setText("" + numLikes);
        layComments.gravity = Gravity.END;
        likes.setLayoutParams(layComments);
        txtLikes.add(likes);

        lin4.addView(likes);
        lin4.addView(comments);


        //botao de like e comment
        LinearLayout lin5 = new LinearLayout(this);
        LinearLayout.LayoutParams layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lin5.setOrientation(LinearLayout.HORIZONTAL);
        lin5.setLayoutParams(layparam);


        ImageButton likeBtn = new ImageButton(this);
        likeBtn.setTag(username + index);
        likeBtn.setBackgroundColor(Color.TRANSPARENT);
        likeBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        likeBtn.setBackgroundResource(R.drawable.like);
        LinearLayout lay = new LinearLayout(this);
        LinearLayout.LayoutParams layAround = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        lay.setLayoutParams(layAround);
        int dpPad38 = (int) UserInfo.convertDpToPixel(38, getApplicationContext());
        int dpPad32 = (int) UserInfo.convertDpToPixel(32, getApplicationContext());
        int dpPad34 = (int) UserInfo.convertDpToPixel(34, getApplicationContext());
        LinearLayout.LayoutParams layImg = new LinearLayout.LayoutParams(dpPad38, dpPad32);
        layImg.setMargins(dpPad3, dpPad5, 0, 0);
        likeBtn.setLayoutParams(layImg);
        lay.addView(likeBtn);
        btnsLike.add(likeBtn);

        ImageButton commentBtn = new ImageButton(this);
        commentBtn.setBackgroundColor(Color.TRANSPARENT);
        commentBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        commentBtn.setBackgroundResource(R.drawable.comment);
        LinearLayout.LayoutParams layImg2 = new LinearLayout.LayoutParams(dpPad34, dpPad32);
        layImg2.setMargins(0, dpPad5, dpPad3, dpPad5);
        commentBtn.setLayoutParams(layImg2);
        btnsComments.add(commentBtn);

        lin5.addView(lay);
        lin5.addView(commentBtn);
        linIcons.add(lin5);

        lin.addView(lin2);
        lin.addView(lin3);
        lin.addView(lin4);
        lin.addView(lin5);

        linConteudos.add(lin);

        linContent.addView(lin);
    }

    private void generateComments(long count, String userIndex, int indexLayout) {
        ArrayList<LinearLayout> linStore = new ArrayList<LinearLayout>();
        ArrayList<TextView> linCom = new ArrayList<TextView>();
        for (int i = 0; i < count; i++) {
            LinearLayout linC = getGenericLinearLayout();

            LinearLayout.LayoutParams layMatch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
            int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
            TextView commentTxt = new TextView(this);
            commentTxt.setPadding(dpPad5, dpPad10, dpPad5, dpPad10);
            commentTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            commentTxt.setLayoutParams(layMatch);
            linCom.add(commentTxt);
            linC.addView(commentTxt);
            linC.setVisibility(View.GONE);
            linConteudos.get(indexLayout).addView(linC);
            linStore.add(linC);

        }
        commentsPerPost.put(userIndex, linStore);
        txtComments.put(userIndex, linCom);
    }

    private void setTextToComments(String userIndex) {
        for (int i = 0; i < commentsStored.get(userIndex).size(); i++) {
            txtComments.get(userIndex).get(i).setText(commentsStored.get(userIndex).get(i));
        }
    }

    private void createWriteComment(int indexLayout) {
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //layout do texto
        LinearLayout lin = new LinearLayout(this);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setWeightSum(11f);
        lin.setLayoutParams(layParams);

        EditText edit = new EditText(this);
        LinearLayout.LayoutParams layEdit = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 7f);

        int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
        int dpPad20 = (int) UserInfo.convertDpToPixel(20, getApplicationContext());
        edit.setPadding(dpPad5, dpPad20, 0, dpPad20);
        edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        edit.setHint(R.string.writeComment);
        edit.setInputType(InputType.TYPE_CLASS_TEXT);
        edit.setLayoutParams(layEdit);


        Button btnSubmit = new Button(this);
        LinearLayout.LayoutParams layBtn = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f);
        btnSubmit.setText(R.string.submit);

        btnSubmit.setLayoutParams(layBtn);

        lin.addView(edit);
        lin.addView(btnSubmit);
        lin.setVisibility(View.GONE);

        commentWrote[indexLayout] = edit;
        btnSubmitComment[indexLayout] = btnSubmit;

        linConteudos.get(indexLayout).addView(lin);
        layoutWriteCommentFixed[indexLayout] = lin;
    }

    private LinearLayout getGenericLinearLayout() {
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //layout do texto
        LinearLayout lin = new LinearLayout(this);
        lin.setBackgroundResource(R.drawable.text_lines);
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setLayoutParams(layParams);

        return lin;
    }

    private void addEmptySpace() {
        int dpPad56 = (int) UserInfo.convertDpToPixel(56, getApplicationContext());

        ImageButton commentBtn = new ImageButton(this);
        commentBtn.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams layImg2 = new LinearLayout.LayoutParams(dpPad56, dpPad56);
        commentBtn.setLayoutParams(layImg2);

        linContent.addView(commentBtn);
    }


    private void constructContentWithDB() {
        dbRef.child(UserInfo.PUBLICATIONS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        usernamePost.add(snapshot.getKey());
                        indexPost.add(snap.getKey());
                        historyPost.add((String) snap.child(UserInfo.STORY_ACCESS).getValue());
                        likesPost.add((Long) snap.child(UserInfo.LIKES_ACCESS).getValue());
                        timerOrder.add(Long.parseLong((String) Objects.requireNonNull(snap.child(UserInfo.TIMER_ACCESS).getValue())));
                    }

                }
                int[] ordered;
                if(!flagRank){
                    ordered = getOrderedPublications(timerOrder);
                }else{
                    ordered = getOrderedPublications(likesPost);
                }

                layoutWriteCommentFixed = new LinearLayout[usernamePost.size()];
                commentWrote = new EditText[usernamePost.size()];
                btnSubmitComment = new Button[usernamePost.size()];
                for (int i = 0; i < usernamePost.size(); i++) {
                    createMainLinearLayout(usernamePost.get(ordered[i]), indexPost.get(ordered[i]), historyPost.get(ordered[i]), likesPost.get(ordered[i]));
                    addListenerToLikes(usernamePost.get(ordered[i]), indexPost.get(ordered[i]), i);
                    getLastThreeComments(usernamePost.get(ordered[i]), indexPost.get(ordered[i]), i);
                }
                addEmptySpace();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void addListenerToLikes(final String username, final String index, final int listIndex) {
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
                                txtLikes.get(listIndex).setText("" + likes);
                            }
                        } else {
                            Long likes = (Long) dataSnapshot.child(UserInfo.LIKES_ACCESS).getValue() + 1;
                            dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.LIKES_ACCESS).setValue(likes);
                            dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.USER_LIKES_ACCESS).child(UserInfo.username).setValue(true);
                            txtLikes.get(listIndex).setText("" + likes);
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


    private void getLastThreeComments(final String username, final String index, final int listIndex) {
        dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(UserInfo.COMMENTS_ACCESS)) {
                    dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.COMMENTS_ACCESS).limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {//
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            generateComments(dataSnapshot.getChildrenCount(), username + index, listIndex);
                            ArrayList<String> storeComments = new ArrayList<String>();
                            ArrayList<TextView> storeCommentsTxt = txtComments.get(username + index);
                            commentsTxt.get(listIndex).setText(dataSnapshot.getChildrenCount() + " comments");
                            int counter = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                storeComments.add((String) snapshot.child(UserInfo.MESSAGE_GROUP_ACCESS).getValue());
                                storeCommentsTxt.get(counter).setText((String) snapshot.child(UserInfo.MESSAGE_GROUP_ACCESS).getValue());
                                counter++;
                            }
                            commentsStored.put(username + index, storeComments);
                            txtComments.put(username + index, storeCommentsTxt);
                            addListenerToComments(username, index, listIndex);
                            setTextToComments(username + index);
                            createWriteComment(listIndex);//adiciona a edição da pergunta
                            addListenerToSubmitComment(username, index, listIndex);//adiciona listener para o botão submit
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("cancel", "onCancelled", error.toException());
                        }

                    });
                } else {
                    commentsTxt.get(listIndex).setText("0 comments");
                    createWriteComment(listIndex);//adiciona a edição da pergunta
                    addListenerToCommentsWithoutComments(listIndex);
                    addListenerToSubmitComment(username, index, listIndex);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
            }
        });
    }

    private void addListenerToComments(final String username, final String index, final int indexList) {
        btnsComments.get(indexList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sizeComments = commentsPerPost.get(username + index).size();
                if (commentsPerPost.get(username + index).get(0).getVisibility() == View.GONE) {
                    for (int i = 0; i < sizeComments; i++) {
                        commentsPerPost.get(username + index).get(i).setVisibility(View.VISIBLE);
                        linIcons.get(indexList).setBackgroundResource(R.drawable.text_lines);
                    }
                    layoutWriteCommentFixed[indexList].setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < sizeComments; i++) {
                        commentsPerPost.get(username + index).get(i).setVisibility(View.GONE);
                        linIcons.get(indexList).setBackgroundResource(0);
                    }
                    layoutWriteCommentFixed[indexList].setVisibility(View.GONE);
                }
            }

        });

    }

    private void addListenerToCommentsWithoutComments(final int indexList) {
        btnsComments.get(indexList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutWriteCommentFixed[indexList].getVisibility() == View.GONE) {
                    layoutWriteCommentFixed[indexList].setVisibility(View.VISIBLE);
                    linIcons.get(indexList).setBackgroundResource(R.drawable.text_lines);
                } else {
                    layoutWriteCommentFixed[indexList].setVisibility(View.GONE);
                    linIcons.get(indexList).setBackgroundResource(0);
                }
            }

        });

    }

    private void addListenerToSubmitComment(final String username, final String index, final int indexList) {

        btnSubmitComment[indexList].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSubmitComment(commentWrote[indexList].getText().toString())) {
                    dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.COMMENTS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long indexCount = dataSnapshot.getChildrenCount();
                            dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.COMMENTS_ACCESS).child(""+indexCount).child(UserInfo.MESSAGE_GROUP_ACCESS)
                                    .setValue(commentWrote[indexList].getText().toString());
                            dbRef.child(UserInfo.PUBLICATIONS_ACCESS).child(username).child(index).child(UserInfo.COMMENTS_ACCESS).child(""+indexCount).child(UserInfo.USER_GROUP_ACCESS)
                                    .setValue(UserInfo.username);
                            createLayoutToSubmitComment(username,index,indexList);//TODO
                            Toast.makeText(Content.this, "Success submitting the comment", Toast.LENGTH_SHORT).show();
                            commentWrote[indexList].setText(null);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("cancel", "onCancelled", error.toException());
                        }
                    });
                } else
                    Toast.makeText(Content.this, "The comment is not valid", Toast.LENGTH_SHORT).show();
            }

        });
    }

    //TODO
    private void createLayoutToSubmitComment(String username, String index, int listIndex){
        ArrayList<String> storeComments = commentsStored.get(username+index);
        ArrayList<String> newComments = new ArrayList<>();
        if(storeComments != null) {
            if (storeComments.size() == 3) {
                for (int i = 0; i < 3; i++) {
                    if (i == 2) {
                        newComments.add(commentWrote[listIndex].getText().toString());
                    } else {
                        newComments.add(storeComments.get(i + 1));
                    }
                }
                commentsStored.put(username + index, newComments);
                for (int i = 0; i < commentsStored.get(username + index).size(); i++) {
                    txtComments.get(username + index).get(i).setText(newComments.get(i));
                }
            } /*else if (storeComments.size() != 0) {
                storeComments.add(commentWrote[listIndex].getText().toString());
                commentsStored.put(username + index, storeComments);
                ArrayList<LinearLayout> linStore = commentsPerPost.get(username + index);

                LinearLayout linC = getGenericLinearLayout();

                LinearLayout.LayoutParams layMatch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                int dpPad5 = (int) UserInfo.convertDpToPixel(5, getApplicationContext());
                int dpPad10 = (int) UserInfo.convertDpToPixel(10, getApplicationContext());
                TextView commentTxt = new TextView(this);
                commentTxt.setPadding(dpPad5, dpPad10, dpPad5, dpPad10);
                commentTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                commentTxt.setLayoutParams(layMatch);
                commentTxt.setText(commentWrote[listIndex].getText().toString());
                linC.addView(commentTxt);
                linConteudos.get(listIndex).addView(linC);
                linStore.add(linC);
                commentsPerPost.put(username + index, linStore);
            }*/
        }
    }

    private boolean validateSubmitComment(String txt) {
        return !(txt.length() < 1 || txt.equals(" "));
    }

    private void setProfilePic(){
        dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserInfo.PROFILE_PICTURE_ACCESS)){
                    dbRef.child(UserInfo.USERS_ACCESS).child(UserInfo.username).child(UserInfo.PROFILE_PICTURE_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Picasso.with(Content.this).load(String.valueOf(dataSnapshot.getValue())).fit().centerInside().into(profileBtn);
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

    // ordena do mais recente para o mais antigo
    private int[] getOrderedPublications(ArrayList<Long> timers) {
        long[] timeOrder = new long[timers.size()];

        for (int i = 0; i < timers.size(); i++) {
            timeOrder[i] = timers.get(i);
        }

        long[] copyVal = timeOrder.clone();

        for (int i = 0; i < timeOrder.length; i++) {
            for (int j = 0; j < timeOrder.length; j++) {
                if (!(timeOrder[i] < timeOrder[j])) {
                    long previous = timeOrder[i];
                    timeOrder[i] = timeOrder[j];
                    timeOrder[j] = previous;
                }
            }
        }

        HashMap<Long, Integer> map = new HashMap<>();// valor numero de repetições
        ArrayList<Long> cheked = new ArrayList<>();

        for (int i = 0; i < timeOrder.length; i++) {
            boolean flag = false;
            for (int j = 0; j < cheked.size(); j++) {
                if (timeOrder[i] == cheked.get(j)) {
                    flag = true;
                    break;
                }

            }
            if (flag)
                continue;

            for (int j = 0; j < timeOrder.length; j++) {
                if (timeOrder[i] == timeOrder[j] && i != j) {
                    if (map.get(timeOrder[i]) != null) {
                        int index = map.get(timeOrder[i]);
                        index++;
                        map.put(timeOrder[i], index);
                    } else
                        map.put(timeOrder[i], 2);

                }
                if (j == timeOrder.length - 1)
                    cheked.add(timeOrder[i]);
            }
        }

        HashMap<Long, Integer> map2 = new HashMap<>();//numero contador

        int[] indexOrder = new int[timers.size()];
        for (int i = 0; i < timeOrder.length; i++) {
            if (map.get(timeOrder[i]) != null) {
                if(map2.get(timeOrder[i]) == null) {
                    map2.put(timeOrder[i], 1);
                    for (int j = 0; j < timeOrder.length; j++) {
                        if (timeOrder[i] == copyVal[j]) {
                            indexOrder[i] = j;
                            break;
                        }
                    }
                }else {
                    int counter = map2.get(timeOrder[i]);
                    map2.put(timeOrder[i],counter+1);
                    for (int j = 0; j < timeOrder.length; j++) {
                        if (timeOrder[i] == copyVal[j]) {
                            if(counter == 0) {
                                indexOrder[i] = j;
                                break;
                            }else
                                counter--;
                        }
                    }

                }
            }
            else {
                for (int j = 0; j < timeOrder.length; j++) {
                    if (timeOrder[i] == copyVal[j]) {
                        indexOrder[i] = j;
                        break;
                    }
                }
            }
        }
        return indexOrder;
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
