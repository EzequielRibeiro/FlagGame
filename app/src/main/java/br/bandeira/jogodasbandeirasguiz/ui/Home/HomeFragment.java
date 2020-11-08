package br.bandeira.jogodasbandeirasguiz.ui.Home;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import br.bandeira.jogodasbandeirasguiz.R;
import br.bandeira.jogodasbandeirasguiz.Score;
import br.bandeira.jogodasbandeirasguiz.StableArrayAdapter;
import br.bandeira.jogodasbandeirasguiz.ui.Start.StartViewModel;


public class HomeFragment extends Fragment {

    private static final int RC_LEADERBOARD_UI = 9004;
    public static int RC_SIGN_IN = 9001;
    public static String TAG = "Flag Game";
    public static boolean isLogged = false;
    private HomeViewModel homeViewModel;
    private ListView listView;
    private View root;
    private StableArrayAdapter ADAPTER;
    private List<Score> items;
    private Button buttonStart, buttonScoreGlobal;
    private NavController navController;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        buttonScoreGlobal = root.findViewById(R.id.buttonScoreGlobal);
        buttonScoreGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLogged) {
                    showLeaderboard();
                } else {
                    if (isGooglePlayServicesAvailable())
                        startSignInIntent();
                }
            }
        });

        final TextView textView = root.findViewById(R.id.text_home);
        textView.setText("SCORE");
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // textView.setText(s);
            }
        });

        buttonStart = root.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.popBackStack(R.id.nav_start, true);
                navController.navigate(R.id.nav_start);

            }
        });

        items = new ArrayList<>();
        items.addAll(StartViewModel.getScoreRecord(root.getContext()));
        listView = root.findViewById(R.id.listView);
        ADAPTER = new StableArrayAdapter(root.getContext(),
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(ADAPTER);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-0822808376839371/8886805116");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                ConstraintLayout constraintLayout = root.findViewById(R.id.constraintLayout_frame_home);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
                params.height = 0;
                mAdView.setLayoutParams(params);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        return root;
    }

    public void updateUI(GoogleSignInAccount account) {

        if (account != null) {
            Toast.makeText(getContext(), "Signed Google Game Play successfully", Toast.LENGTH_LONG).show();
            isLogged = true;
        } else {
            Toast.makeText(getContext(), "Signed Google Game Play failed", Toast.LENGTH_LONG).show();
            isLogged = false;
        }

    }

    public void onResume() {
        super.onResume();
        navController.popBackStack(R.id.nav_start, true);

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        if (isGooglePlayServicesAvailable())
              signInSilently();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, requestCode + ":" + resultCode + ":" + RC_SIGN_IN);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                updateUI(signedInAccount);
                showLeaderboard();

            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this.getContext()).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    public void signInSilently() {

        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            GoogleSignInAccount signedInAccount = account;
            updateUI(signedInAccount);
            // currentPlayer(signedInAccount);
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(getContext(), signInOptions);
            signInClient
                    .silentSignIn()
                    .addOnCompleteListener(
                            getActivity(),
                            new OnCompleteListener<GoogleSignInAccount>() {
                                @Override
                                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                    if (task.isSuccessful()) {
                                        // The signed in account is stored in the task's result.
                                        GoogleSignInAccount signedInAccount = task.getResult();
                                        updateUI(signedInAccount);

                                    } else {
                                        updateUI(null);
                                    }
                                }
                            });
        }
    }

    private void showLeaderboard() {
        Games.getLeaderboardsClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getContext()))
                .getLeaderboardIntent(getString(R.string.leaderboard_id))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(getContext(),
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        isLogged = false;
                    }
                });
    }

    private void startSignInIntent() {

        GoogleSignInClient signInClient = GoogleSignIn.getClient(getContext(),
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googlePlayServicesAvailability = GoogleApiAvailability.getInstance();

        if (googlePlayServicesAvailability == null) {
            Log.e(TAG, "Google Play Services Availability Failed");
            return false;
        } else {
            Log.e(TAG, "Google Play Services Availability Working.");
        }

        return googlePlayServicesAvailability.isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
    }

    public void onDestroy() {
        super.onDestroy();

    }

}
