package br.bandeira.jogodasbandeirasguiz.ui.Rank;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PlayGamesAuthProvider;

import java.util.HashMap;
import java.util.Map;

import br.bandeira.jogodasbandeirasguiz.R;

import static br.bandeira.jogodasbandeirasguiz.ui.Home.HomeFragment.RC_SIGN_IN;
import static br.bandeira.jogodasbandeirasguiz.ui.Home.HomeFragment.TAG;

public class RankFragment extends Fragment  {

    private RankViewModel mViewModel;

    private Button        button;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_rank, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RankViewModel.class);

    }


    private void currentPlayer(final GoogleSignInAccount acc){
        PlayersClient playersClient = Games.getPlayersClient(getActivity(), acc);
        playersClient.getCurrentPlayer().addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                Map<String, Object> successMap = new HashMap<>();
                successMap.put("type", "SUCCESS");
                successMap.put("id", player.getPlayerId());
                successMap.put("email", acc.getEmail());
                successMap.put("displayName", player.getDisplayName());
                successMap.put("hiResImageUri", player.getHiResImageUri().toString());
                successMap.put("iconImageUri", player.getIconImageUri().toString());
                Log.e(TAG,successMap.toString());
                firebaseAuthWithPlayGames(acc);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,"ERROR_FETCH_PLAYER_PROFILE: " + e);
            }
        });


    }





    @Override
    public void onResume() {
        super.onResume();



    }

    private void firebaseAuthWithPlayGames(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithPlayGames:" + acct.getId());

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = PlayGamesAuthProvider.getCredential(acct.getServerAuthCode());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure" + task.getException());
                         //   updateUI(null);
                        }


                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();

    }

}