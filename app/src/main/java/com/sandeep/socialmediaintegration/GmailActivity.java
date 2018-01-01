package com.sandeep.socialmediaintegration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class GmailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int SIGN_IN = 30;
    private GoogleSignInOptions signInOptions;
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private TextView tv;
    private ImageView iv;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail);

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInButton = findViewById(R.id.sign_in_button);
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .addApi(Plus.API)
                .build();

        signInButton.setOnClickListener(view -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, SIGN_IN);
        });

        tv = findViewById(R.id.text);
        iv = findViewById(R.id.iv);
        Button btn = findViewById(R.id.btn);
        aQuery = new AQuery(this);

        btn.setOnClickListener(view -> Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(status -> Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show()));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("@sandy","Failure: "+connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @SuppressLint("SetTextI18n")
    private void handleSignInResult(GoogleSignInResult result) {
        Log.i("@sandy","ResultSuccess: "+result.isSuccess());
        Log.i("@sandy","ResultSignInAccount: "+result.getSignInAccount());
        Log.i("@sandy","ResultStatus: "+result.getStatus());

        if (result.isSuccess()){
            final GoogleSignInAccount acct = result.getSignInAccount();
            String name = acct.getDisplayName();
            final String mail = acct.getEmail();
            // String photourl = acct.getPhotoUrl().toString();
            final String givenname="",familyname="",displayname="",birthday="";
            Plus.PeopleApi.load(googleApiClient, acct.getId()).setResultCallback(loadPeopleResult -> {
                Person person = loadPeopleResult.getPersonBuffer().get(0);
                Log.i("@sandy ", person.getName().getGivenName());
                Log.i("@sandy ",person.getName().getFamilyName());
                Log.i("@sandy ",person.getDisplayName());
                Log.i("@sandy ", String.valueOf(person.getGender())); //0 = male 1 = female
                String gender="";
                if(person.getGender() == 0){
                    gender = "Male";
                }else {
                    gender = "Female";
                }
                if(person.hasBirthday()){
                    tv.setText(person.getName().getGivenName()+" \n"+person.getName().getFamilyName()+" \n"+gender+"\n"+person.getBirthday());
                }else {
                    tv.setText(person.getName().getGivenName()+" \n"+person.getName().getFamilyName()+" \n"+gender);
                }
                aQuery.id(iv).image(acct.getPhotoUrl().toString());
                Log.i("@sandy",acct.getPhotoUrl().toString());
              /*   Log.d(TAG,"CurrentLocation "+person.getCurrentLocation());
                Log.d(TAG,"AboutMe "+person.getAboutMe());*/
                // Log.d("Birthday ",person.getBirthday());
                // Log.d(TAG,"Image "+person.getImage());
            });
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }
}
