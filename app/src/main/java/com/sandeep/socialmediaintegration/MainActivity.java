package com.sandeep.socialmediaintegration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final String KEY_FB_NAME = "key_fb_name";
    private static final String KEY_FB_EMAIL = "key_fb_email";
    private static final String KEY_FB = "key_fb";
    private CallbackManager callbackManager;
    boolean loggedIn = AccessToken.getCurrentAccessToken() == null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("FB Integration");

        getKeyHash();
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginBtn = findViewById(R.id.btnFbLogin);
        Button button = findViewById(R.id.btnGmailLogin);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,GmailActivity.class);
            startActivity(intent);
        });
        loginBtn.setReadPermissions("email");
        loginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("@sandy","OnSuccess"+loginResult.toString());
                getUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                Log.i("@sandy","OnCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("@sandy","OnError"+error);
            }
        });
        loginBtn.setOnClickListener(view -> onBtnClick());
    }

    private void onBtnClick() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getKeyHash() {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sandeep.socialmediaintegration",
                    PackageManager.GET_SIGNATURES);
            Log.i("@sandy","Key");
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("@sandy:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        } catch (NoSuchAlgorithmException ignored) {
        }
    }

    private void getUserDetails(LoginResult loginResult) {
        Log.i("@sandy","getUserDetails: "+loginResult.getAccessToken());
        GraphRequest graphRequest=GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {
                    Intent intent = new Intent(this,NextFB.class);
                    Log.i("@sandy","OnCompletedResp: "+response);
                    Log.i("@sandy"," Object: "+object);
                    Toast.makeText(MainActivity.this, " Done ", Toast.LENGTH_SHORT).show();
                    String jsonData = object.toString();
                    try{
                        JSONObject jsonObject = new JSONObject(jsonData);
                        Log.i("@sandy","FB Email: "+jsonObject.get("email").toString());
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_FB_NAME,jsonObject.get("name").toString());
                        bundle.putString(KEY_FB_EMAIL,jsonObject.get("email").toString());
                        Log.i("@sandy","JSON_OBJ: "+object.toString());
                        intent.putExtra(KEY_FB,bundle);
                        startActivity(intent);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                });
        Bundle bundle_param = new Bundle();
        bundle_param.putString("fields","id,name,email");
        graphRequest.setParameters(bundle_param);
        graphRequest.executeAsync();
    }
}
