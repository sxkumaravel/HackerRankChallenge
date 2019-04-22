package loginscreen.solution.example.com.loginscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String EMAIL_REGIX = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";

    private static final String KEY_SAVED = "key_saved";

    private LinearLayout mNameLayout;
    private EditText mNameET;
    private EditText mEmailET;
    private EditText mPasswordET;
    private EditText mPhoneET;

    private Button mSignUp;
    private Button mLogin;

    private ViewFlipper mViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        mLogin = (Button) findViewById(R.id.bt_login);
        mLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.buttoncolor));
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNameLayout.setVisibility(View.INVISIBLE);
                mViewFlipper.showPrevious();

                mLogin.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.buttoncolor));
                mSignUp.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
            }
        });

        mSignUp = (Button) findViewById(R.id.bt_signup);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNameLayout.setVisibility(View.VISIBLE);
                mViewFlipper.showNext();

                mLogin.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                mSignUp.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.buttoncolor));
            }
        });

        findViewById(R.id.bt_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignIn();
            }
        });

        findViewById(R.id.bt_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleProfileCreation();
            }
        });

        mNameLayout = (LinearLayout) findViewById(R.id.lt_name);

        mNameET = (EditText) findViewById(R.id.et_name);
        mEmailET = (EditText) findViewById(R.id.et_email);
        mPasswordET = (EditText) findViewById(R.id.et_password);
        mPhoneET = (EditText) findViewById(R.id.et_phone);

        SpacesInputFilter spaceFilter = new SpacesInputFilter(this);
        mEmailET.setFilters(new InputFilter[]{spaceFilter});
    }

    private void handleProfileCreation() {
        String name = mNameET.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_LONG).show();
            return;
        }

        String email = mEmailET.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isAValidEmailAddress(email)) {
            Toast.makeText(this, "Email is invalid", Toast.LENGTH_LONG).show();
            return;
        }

        // validate password
        String password = mPasswordET.getText().toString().trim();
        if (password.length() < 6) {
            Toast.makeText(this, "Minimum password length 6", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Not a valid Password.\nOne or More Uppercase.\nOne or More Lowercase.\nOne or More Digit.\nOne or More Special Character.", Toast.LENGTH_LONG).show();
            return;
        }

        String phone = mPhoneET.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Phone is required", Toast.LENGTH_LONG).show();
            return;
        }

        if (phone.length() != 10) {
            Toast.makeText(this, "Phone number is invalid", Toast.LENGTH_LONG).show();
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);

        saveToLocal(user);

        Intent intent = new Intent(this, LoginWelcomeActivity.class);
        intent.putExtra(LoginWelcomeActivity.EXTRA_USER_INFO, user);
        startActivity(intent);
    }

    private void saveToLocal(@NonNull User user) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String string = preferences.getString(KEY_SAVED, "");

        List<User> userList;
        if (TextUtils.isEmpty(string)) {
            userList = new ArrayList<>();
        } else {
            Type listType = new TypeToken<List<User>>() {
            }.getType();
            userList = new Gson().fromJson(string, listType);
        }

        userList.add(user);

        preferences.edit().putString(KEY_SAVED, new Gson().toJson(userList)).apply();
    }

    private void handleSignIn() {
        String email = mEmailET.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isAValidEmailAddress(email)) {
            Toast.makeText(this, "Email is invalid", Toast.LENGTH_LONG).show();
            return;
        }

        // validate password
        String password = mPasswordET.getText().toString().trim();
        if (password.length() < 6) {
            Toast.makeText(this, "Minimum password length 6", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Not a valid Password.\nOne or More Uppercase.\nOne or More Lowercase.\nOne or More Digit.\nOne or More Special Character.", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String string = preferences.getString(KEY_SAVED, "");

        if (TextUtils.isEmpty(string)) {
            Toast.makeText(this, "User name or Password is wrong or doesn't match", Toast.LENGTH_LONG).show();
            return;
        }

        Type listType = new TypeToken<List<User>>() {
        }.getType();
        List<User> userList = new Gson().fromJson(string, listType);
        if (userList == null || userList.isEmpty()) {
            Toast.makeText(this, "User name or Password is wrong or doesn't match", Toast.LENGTH_LONG).show();
            return;
        }

        User userFound = null;
        for (User user : userList) {
            if (email.equalsIgnoreCase(user.getEmail()) && password.equals(user.getPassword())) {
                userFound = user;
                break;
            }
        }

        if (userFound == null) {
            Toast.makeText(this, "User name or Password is wrong or doesn't match", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, LoginWelcomeActivity.class);
        intent.putExtra(LoginWelcomeActivity.EXTRA_USER_INFO, userFound);
        startActivity(intent);
    }

    private boolean isAValidEmailAddress(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGIX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
