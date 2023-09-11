package br.bandeira.jogodasbandeirasguiz.ui.Home;


import static br.bandeira.jogodasbandeirasguiz.ui.Start.StartViewModel.pauseChronometer;
import static br.bandeira.jogodasbandeirasguiz.ui.Start.StartViewModel.resetChronometer;
import static br.bandeira.jogodasbandeirasguiz.ui.Start.StartViewModel.startChronometer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import br.bandeira.jogodasbandeirasguiz.MainActivity;
import br.bandeira.jogodasbandeirasguiz.R;
import br.bandeira.jogodasbandeirasguiz.Score;
import br.bandeira.jogodasbandeirasguiz.StableArrayAdapter;
import br.bandeira.jogodasbandeirasguiz.ui.Start.StartViewModel;


public class HomeFragment extends Fragment {

    private static final int RC_LEADERBOARD_UI = 9004;
    public static int RC_SIGN_IN = 9001;

    public static long RAWSCORE = 0;
    public static String playerName = "";
    public static String serverAuthToken = "";
    public static String TAG = "Flag Game";
    public static boolean isAuthenticated = false;
    private HomeViewModel homeViewModel;
    private ListView listView;
    private View root;
    private StableArrayAdapter ADAPTER;
    private List<Score> items;
    private Button buttonStart, buttonScoreGlobal, buttonSign;
    private NavController navController;
    private AdView mAdView;
    public static InterstitialAd mInterstitialAd;

    public static GamesSignInClient gamesSignInClient;

    private TextView textViewId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_controller_fragment);
        NavigationView navigationView = ((MainActivity)getActivity()).findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        buttonSign = headerView.findViewById(R.id.buttonSigned);
        textViewId = root.findViewById(R.id.textViewIdPlayer);


        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                String value = button.getText().toString();

                if(value.equalsIgnoreCase("Sign Out")){
                     signOut();
                }else
                    signInSilently();
            }

        });
        buttonScoreGlobal = root.findViewById(R.id.buttonScoreGlobal);
        buttonScoreGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAuthenticated) {
                     showLeaderboard();
                } else{
                   //  gamesSignInClient(getActivity());
                    Toast toast = Toast.makeText(getActivity(),"Player is not signed in on Google Play. Press Menu and Sign In",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Login Google Play");
                    builder.setMessage("Player is not signed in on Google Play. Press Sign In to see the score of the others players.");
                    builder.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            gamesSignInClient();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            buttonSign.setText("Sign In");
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

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

                if(mInterstitialAd != null)
                    showInterstitial(getContext());

              //  Navigation.findNavController(v).navigate(R.id.nav_start);

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


        loadAdInter(getContext());

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = root.findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                ConstraintLayout constraintLayout = root.findViewById(R.id.constraintLayout_frame_home);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
                params.height = 0;
                mAdView.setLayoutParams(params);
            }
        });




        if (isGooglePlayServicesAvailable())
              signInSilently();

        return root;
    }

    private static void loadAdInter(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();
        String id = context.getString(R.string.ad_inter_id);
        // id = "ca-app-pub-3940256099942544/1033173712"; // id to test Ad

        InterstitialAd.load(context, id, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        interstitialAdListner();
                        Log.i("InterstitialAd", "loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;

                    }
                });

    }

    private static void interstitialAdListner() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                mInterstitialAd = null;
                resetChronometer();
                startChronometer();
                Log.d("TAG", "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                mInterstitialAd = null;

                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null;
                pauseChronometer();
                Log.d("TAG", "The ad was shown.");
            }
        });


    }

    public static void showInterstitial(Context context) {

        Activity activity = (Activity) context;

        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
            Log.d("InterstitialAd", "not null");
        } else {
            Log.d("InterstitialAd", "null");
        }

    }

    public void updateUI(boolean isSuccessful) {

        if (isSuccessful) {
            // Toast.makeText(getContext(), "Signed Google Game Play successfully", Toast.LENGTH_LONG).show();
            Log.i(TAG,"Signed Google Game Play successfully");
            isAuthenticated = true;
            buttonSign.setText("Sign Out");

        } else {
            Log.i(TAG,"Signed Google Game Play failed");
            isAuthenticated = false;
            buttonSign.setText("Sign in");
        }

    }


    public static void loadScoreOfLeaderBoard(Activity activity) {

        PlayGames.getLeaderboardsClient(activity).loadCurrentPlayerLeaderboardScore(activity.getString(R.string.leaderboard_id),
                        LeaderboardVariant.TIME_SPAN_ALL_TIME,LeaderboardVariant.COLLECTION_PUBLIC).addOnSuccessListener(leaderboardScoreAnnotatedData -> {
                            LeaderboardScore scoresResult =  leaderboardScoreAnnotatedData.get();
                            RAWSCORE = (scoresResult != null ? scoresResult.getRawScore() : 0);
                        });
    }

    public void onResume() {
        super.onResume();
        navController.popBackStack(R.id.nav_start, true);

        if(isAuthenticated) {
            buttonSign.setText("Sign Out");
            loadScoreOfLeaderBoard(getActivity());
        }
        else
            buttonSign.setText("Sign in");

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
                if(signedInAccount != null)
                    if (signedInAccount.isExpired()) {
                        updateUI(false);

                    }
                    else {
                        updateUI(true);


                    }


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

    public String getPlayeriD(){

        PlayGames.getPlayersClient(getActivity()).getCurrentPlayer().addOnCompleteListener(mTask -> {
                  playerName = mTask.getResult().getDisplayName();
                }
        );
        return playerName;
    }

    public void signInSilently() {

        boolean isDebuggable =  ( 0 != ( getActivity().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
        String apiGoogleGame;

        if(isDebuggable)
            apiGoogleGame = getString(R.string.OAuth_Client_Id_Debug);
        else
            apiGoogleGame = getString(R.string.OAuth_Client_Id);

       // Toast.makeText(getActivity(),"debug:"+isDebuggable,Toast.LENGTH_LONG).show();

        Log.e(TAG,"Compiler: "+isDebuggable);
        gamesSignInClient = PlayGames.getGamesSignInClient(getActivity());

         gamesSignInClient = PlayGames.getGamesSignInClient(getActivity());
         gamesSignInClient
                .requestServerSideAccess(apiGoogleGame,
                         true)
                .addOnCompleteListener( task -> {

                    if (task.isSuccessful()) {
                        serverAuthToken = task.getResult();
                    }
                    isAuthenticated = gamesSignInClient.isAuthenticated().isSuccessful();
                    updateUI(isAuthenticated);

                    if(isAuthenticated) {
                        buttonSign.setText("Sign Out");
                        loadScoreOfLeaderBoard(getActivity());
                    }else
                        buttonSign.setText("Sign In");

                });

    }

    public void gamesSignInClient (){
        if(gamesSignInClient != null)
                 gamesSignInClient.signIn();
        isAuthenticated = gamesSignInClient.isAuthenticated().isSuccessful();
        if(isAuthenticated)
            Toast.makeText(getActivity(),"Signed Google Game Play successfully",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(),"Signed Google Game Play failed",Toast.LENGTH_LONG).show();

        updateUI(isAuthenticated);
    }

    private void showLeaderboard() {
        PlayGames.getLeaderboardsClient(getActivity())
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
                        isAuthenticated = false;
                        buttonSign.setText("Sign In");
                    }
                });
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googlePlayServicesAvailability = GoogleApiAvailability.getInstance();

        if (googlePlayServicesAvailability == null) {
            Log.e(TAG, "Google Play Services Availability Failed");
            return false;
        } else {
            Log.i(TAG, "Google Play Services Availability Working.");
        }

        return googlePlayServicesAvailability.isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS;
    }


    public void onStart() {
        super.onStart();
    }

    public void onDestroy() {
        super.onDestroy();
        signOut();

    }

}
