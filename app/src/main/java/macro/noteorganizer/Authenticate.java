package macro.noteorganizer;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.SignInStateChangeListener;
import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;

public class Authenticate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        AWSMobileClient.getInstance().initialize(Authenticate.this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                AuthUIConfiguration config =
                        new AuthUIConfiguration.Builder()
                                .userPools(true)
                                .logoResId(R.mipmap.ic_launcher_foreground)
                                .backgroundColor(Color.rgb(255, 251, 251))
                                .isBackgroundColorFullScreen(true)
                                .fontFamily("sans-serif-condensed-light")
                                .build();
                IdentityManager.getDefaultIdentityManager().getUserID(new IdentityHandler() {

                    @Override
                    public void onIdentityId(String s) {

                        //The network call to fetch AWS credentials succeeded, the cached
                        // user ID is available from IdentityManager throughout your app
                        Log.d("MainActivity", "Identity ID is: " + s);
                        Log.d("MainActivity", "Cached Identity ID: " + IdentityManager.getDefaultIdentityManager().getCachedUserID());
                    }

                    @Override
                    public void handleError(Exception e) {
                        Log.e("MainActivity", "Error in retrieving Identity ID: " + e.getMessage());
                    }
                });
                SignIn(config);
            }
        }).execute();
    }

    public void SignIn(final AuthUIConfiguration config) {
        SignInUI sign = (SignInUI) AWSMobileClient.getInstance().getClient(Authenticate.this, SignInUI.class);
        IdentityManager.getDefaultIdentityManager().addSignInStateChangeListener(new SignInStateChangeListener() {
            @Override
            public void onUserSignedIn() {
                Log.d("SIGN", "Signed in");
            }

            @Override
            public void onUserSignedOut() {
                Log.d("SIGNO", "Signed out");
                SignIn(config);
            }
        });
        sign.login(Authenticate.this, Notes.class).authUIConfiguration(config).execute();
    }
}
