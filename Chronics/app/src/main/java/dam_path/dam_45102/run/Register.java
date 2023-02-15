package dam_path.dam_45102.run;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dam_path.dam_45102.storage.User;
import dam_path.dam_45102.storage.UserInfo;
import pl.droidsonroids.gif.GifImageView;

public class Register extends AppCompatActivity {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText username;
    private EditText email;
    private EditText password;
    private GifImageView loadingGif;
    private Button registerBtn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        username = findViewById(R.id.box_username);
        email = findViewById(R.id.box_email);
        password = findViewById(R.id.box_password);
        loadingGif = findViewById(R.id.loadingGif);

        mAuth = FirebaseAuth.getInstance();
        mDatabase =FirebaseDatabase.getInstance().getReference();

        registerBtn = findViewById(R.id.btn_register);
        TextView haveAcc = findViewById(R.id.haveAcc);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBtn.setEnabled(false);
                String user = username.getText().toString();
                String emailTxt = email.getText().toString();
                String pass = password.getText().toString();
                String resultOfLog = checkInputUser(user, emailTxt, pass);
                if(!resultOfLog.equals("Register with success")) {
                    Toast.makeText(Register.this, resultOfLog, Toast.LENGTH_LONG).show();
                    registerBtn.setEnabled(true);
                }
                else
                    validateExistintUsername(user, emailTxt, pass);
            }

        });

        haveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

    }

    private String checkInputUser(String username, String email, String password){
        String checkUserPass = MainActivity.checkInputByUser(username,password);

        if(checkUserPass.equals("Success Login")){
            if(validate(email))
                return "Register with success";

            else
                return "The email is not valid";
        }
        else
            return checkUserPass;
    }

    private boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }


    private void checkRegister(final String username, final String email, String pass){
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    writeNewUser(username, email);
                    Toast.makeText(Register.this, "Register with success",Toast.LENGTH_SHORT).show();
                    loadingGif.setVisibility(View.GONE);
                    finish();
                } else {
                    loadingGif.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "The email already exists",Toast.LENGTH_SHORT).show();
                    registerBtn.setEnabled(true);
                }
            }
        });
    }

    private void writeNewUser(String username, String email) {
        User user = new User(email);
        mDatabase.child(UserInfo.USERS_ACCESS).child(username).setValue(user);
    }

    private void validateExistintUsername(final String username, final String email, final String pass){
        loadingGif.setVisibility(View.VISIBLE);
        mDatabase.child(UserInfo.USERS_ACCESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    Toast.makeText(Register.this, "The username already exists", Toast.LENGTH_SHORT).show();
                    loadingGif.setVisibility(View.GONE);
                    registerBtn.setEnabled(true);
                }
                else
                    checkRegister(username,email,pass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("cancel", "onCancelled", error.toException());
                registerBtn.setEnabled(true);
            }
        });
    }

    /*private void setValueToDB() {
        // Write a message to the reference with the instance of myRef
        myRef.setValue("Hello, World!");
    }*/
}
