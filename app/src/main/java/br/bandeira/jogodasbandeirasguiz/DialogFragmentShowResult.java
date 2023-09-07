package br.bandeira.jogodasbandeirasguiz;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import br.bandeira.jogodasbandeirasguiz.ui.Start.StartViewModel;

public class DialogFragmentShowResult extends DialogFragment {


    private int score = 0, hit = 0, flags = 0;
    private boolean win = false;
    private AdView mAdViewResult;

    public DialogFragmentShowResult(){

    }

    public static DialogFragmentShowResult newInstance(boolean win) {
        DialogFragmentShowResult frag = new DialogFragmentShowResult();
        Bundle args = new Bundle();
        args.putInt("score",(int)StartViewModel.SCORE);
        args.putInt("flags",StartViewModel.POSITIONFLAG);
        args.putInt("hit",StartViewModel.HIT);
        args.putBoolean("win",win);
        frag.setArguments(args);

        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        score = getArguments().getInt("score");
        flags = getArguments().getInt("flags");
        hit   = getArguments().getInt("hit");
        win   = getArguments().getBoolean("win");

        final View v = inflater.inflate(R.layout.dialog_show_result, null);

        TextView textDialogScore = (TextView)  v.findViewById(R.id.textDialogScore);
        textDialogScore.setText(Integer.toString(score));

        TextView textDialogFlags = (TextView)  v.findViewById(R.id.textDialogFlags);
        textDialogFlags.setText(Integer.toString(flags));

        TextView textDialogHit = (TextView)  v.findViewById(R.id.textDialogHits);
        textDialogHit.setText(Integer.toString(hit));

        ImageView imageView = (ImageView) v.findViewById(R.id.imageDialogResultWin);


        if(win){
           imageView.setVisibility(View.VISIBLE);
        }else{
		   imageView.setVisibility(View.GONE);
		}

        builder.setView(v)
                // Add action buttons
                .setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    StartViewModel.resetChronometer();
                    StartViewModel.startChronometer();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_controller_fragment);
                        navController.popBackStack();
                        //Navigation.findNavController(view).navigate(R.id.nav_home);
                    }
                });


        mAdViewResult = v.findViewById(R.id.adViewResult);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdViewResult.loadAd(adRequest);
        mAdViewResult.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }


            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                ConstraintLayout constraintLayout = v.findViewById(R.id.constantLayoutDialogResult);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
                params.height = 0;
                mAdViewResult.setLayoutParams(params);

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
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });




        return builder.create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }



}
