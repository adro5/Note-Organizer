package macro.noteorganizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.SignInStateChangeListener;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;

public class Authenticate extends AppCompatActivity {

    AWSCredentialsProvider credentialsProvider;
    AWSConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        AWSMobileClient.getInstance().initialize(this).execute();
        IdentityManager.getDefaultIdentityManager().addSignInStateChangeListener(new SignInStateChangeListener() {
            @Override
            public void onUserSignedIn() {
                Log.d("SIGN", "Signed in");
            }

            @Override
            public void onUserSignedOut() {
                Log.d("SIGNO", "Signed out");
                SignIn();
            }
        });
        SignIn();
    }

    private void SignIn() {
        SignInUI sign = (SignInUI) AWSMobileClient.getInstance().getClient(Authenticate.this, SignInUI.class);
        sign.login(Authenticate.this, Notes.class).execute();
    }
}
