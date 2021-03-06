package mjsma5.budgey;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class UserAccount extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    public FirebaseAuth mAuth;
    // [END declare_auth]

    private ImageView imgPhoto;

    private Button btnDelete;

    private TextView mStatusTextView;

    public static DatabaseReference userRef;

    private DialogInterface.OnClickListener dialogClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        // Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


               // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        imgPhoto = (ImageView) findViewById(R.id.imgProfile);
        userRef = GoogleSignInActivity.userRef;

        // Button listeners
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);

        // Create Alert Dialog for delete account
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        delete();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, GoogleSignInActivity.class);
            startActivity(intent);
        } else {
            updateUI(currentUser);
        }
    }
    // [END on_start_check_user]

    private void signOut(String condition) {
        Intent intent = new Intent(this, GoogleSignInActivity.class);
        intent.putExtra("condition", condition);
        intent.putExtra("stop", true);
        startActivity(intent);
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            new ImageLoadTask(user.getPhotoUrl().toString(), imgPhoto).execute();
            Log.d("SIGN_IN", "SUCCESSFUL");
            imgPhoto.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            btnDelete.setVisibility(View.GONE);
            imgPhoto.setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                break;
            case R.id.sign_out_button:
                signOut("1");
                break;
            case R.id.btnDelete:
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(this);
                deleteAlert.setTitle("WARNING!");
                deleteAlert.setMessage("Are you sure you want to delete your account data? This will" +
                        " remove ALL transactions! ").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
        }
    }

    private void delete() {
        signOut("2");
    }
}
