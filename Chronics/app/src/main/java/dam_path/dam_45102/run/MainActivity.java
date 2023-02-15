package dam_path.dam_45102.run;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dam_path.dam_45102.storage.User;
import dam_path.dam_45102.storage.UserInfo;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private EditText username;
    private EditText password;
    private GifImageView loadingGif;
    private Button login;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private String email;

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.box_username);
        password = findViewById(R.id.box_password);
        loadingGif = findViewById(R.id.loadingGif);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        login = findViewById(R.id.btn_login);
        TextView createAcc = findViewById(R.id.create_acc);
        ImageButton googleLogin = findViewById(R.id.log_google);
        TextView info = findViewById(R.id.info);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //TODO quando ja esta logado isto vai dar nao null
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setEnabled(false);
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String resultOfLog = checkInputByUser(user, pass);

                if (resultOfLog.equals("Success Login"))
                    changeLayout(user, pass);

                else {
                    Toast.makeText(MainActivity.this, resultOfLog, Toast.LENGTH_LONG).show();
                    login.setEnabled(true);
                }
            }

        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }

        });

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Information.class);
                startActivity(intent);
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            checkResultOfLogin(result, task);
        }
    }

    private void checkResultOfLogin(GoogleSignInResult result, Task<GoogleSignInAccount> completedTask) {
        if (result != null) {
            if (result.isSuccess()) {
                handleSignInResult(completedTask);
            } else {
                Toast.makeText(getApplicationContext(), "Sign in cancel", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Data inserted is incorrect", Toast.LENGTH_LONG).show();
        }
    }

    //quando faz login pela google o username é a primeira parte do email
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            String email = account.getEmail();

            String[] converter = email.split("@");
            validateExistintUsername(converter[0], email);

        } catch (ApiException e) {
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    private void validateExistintUsername(final String username, final String email) {
        loadingGif.setVisibility(View.VISIBLE);
        dbRef.child(UserInfo.USERS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (Objects.requireNonNull(snapshot.getValue(User.class)).getEmail().equals(email)) {
                        flag = true;
                        UserInfo.username = snapshot.getKey();
                        break;
                    }
                }

                if (!flag) {
                    if (!dataSnapshot.hasChild(username)) {
                        User user = new User(email);
                        dbRef.child(UserInfo.USERS_ACCESS).child(username).setValue(user);

                    } else {
                        String result = username + "*";
                        User user = new User(email);
                        dbRef.child(UserInfo.USERS_ACCESS).child(result).setValue(user);
                    }
                }
                Intent intent = new Intent(MainActivity.this, Content.class);
                startActivity(intent);
        }

        @Override
        public void onCancelled (@NonNull DatabaseError error){
            Log.e("cancel", "onCancelled", error.toException());
        }
    });
}


    public static String checkInputByUser(String username, String password) {
        if (username.length() < 4) {
            return "Username must have at least 4 characters";
        } else if (username.length() > 30) {
            return "Username must have less than 30 characters";
        } else if (!checkAlphaNumeric(username)) {
            return "The username must be only numbers and letters";
        } else if (password.length() < 6) {
            return "Password must have at least 6 characters";
        } else if (password.length() > 50) {
            return "Password must have less than 50 characters";
        } else {
            return "Success Login";
        }
    }

    public static boolean checkAlphaNumeric(String str) {
        String regex = "^[a-zA-Z0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    private void changeLayout(String user, final String pass) {
        loadingGif.setVisibility(View.VISIBLE);
        convertUsernameToEmail(user,pass);

    }

    private void convertUsernameToEmail(String username, final String pass) {
        dbRef.child(UserInfo.USERS_ACCESS).child(username).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = (String) snapshot.getValue();
                signIn(pass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("dbError", " cancelled");
                Toast.makeText(MainActivity.this, "Error acessing database.", Toast.LENGTH_SHORT).show();
                loadingGif.setVisibility(View.GONE);
            }
        });
    }

    private void signIn(String pass) {
        if (email != null) {
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loadingGif.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Success Login", Toast.LENGTH_LONG).show();
                                UserInfo.username = username.getText().toString();
                                Intent intent = new Intent(MainActivity.this, Content.class);
                                startActivity(intent);

                            } else {
                                loadingGif.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                login.setEnabled(true);
                            }
                        }
                    });
        } else {
            loadingGif.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "The username or password is not correct", Toast.LENGTH_SHORT).show();
            login.setEnabled(true);
        }
    }


}
