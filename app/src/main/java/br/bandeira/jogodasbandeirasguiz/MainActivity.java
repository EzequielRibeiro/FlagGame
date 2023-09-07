package br.bandeira.jogodasbandeirasguiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.view.GravityCompat;
import androidx.multidex.MultiDex;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.games.PlayGamesSdk;
import java.util.Random;

import br.bandeira.jogodasbandeirasguiz.ui.Start.StartViewModel;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public final static String TAG = "FLAG2020";
    private DrawerLayout drawer;
    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView textViewId, textViewVersion;

    private AlertDialog alertDialog;
    private AdView adView;
    private  NavController navController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);
        PlayGamesSdk.initialize(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                loadAdMobExit();
            }
        });
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about, R.id.nav_privacy,R.id.nav_help,R.id.nav_feedback)
                .setDrawerLayout(drawer)
                .build();

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_controller_fragment);
        navController = navHostFragment.getNavController();

       // NavController navController = Navigation.findNavController(this, R.id.nav_host_controller_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.e("destination: ", destination.getLabel().toString());
                if(destination.getLabel().toString().equals(getString(R.string.menu_about))){

                }

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()

                if (id == R.id.nav_feedback) {
                    feedBack();
                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        sharedPreferences = getSharedPreferences("values", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        View headerView = navigationView.getHeaderView(0);
        textViewId = (TextView) headerView.findViewById(R.id.textViewIdPlayer);
        textViewVersion = headerView.findViewById(R.id.textViewVersion);
        textViewVersion.setText(BuildConfig.VERSION_NAME);

        if(!sharedPreferences.contains("id")){
            editor.putString("id","").commit();
        }

        if(!sharedPreferences.contains("audio")){
            editor.putBoolean("audio",true).commit();
        }

    }



    private String getPlayerIdGenerator() {
        Random rand = new Random();
        int i = rand.nextInt(100000);
        String id = getResources().getConfiguration().locale.getCountry()+"-"+i;

        return id ;
    }

    public static void Sound(final Context cont){

        final Context context = cont;
        final SharedPreferences sharedPreferences = context.getSharedPreferences("values", Context.MODE_PRIVATE);
        final boolean audio = sharedPreferences.getBoolean("audio",true);
        final ImageView imageView = new ImageView(context);
        if(audio){
            imageView.setBackground(context.getResources().getDrawable(R.drawable.sound_on));
        }else{
            imageView.setBackground(context.getResources().getDrawable(R.drawable.sound_off));
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(audio){
                    imageView.setBackground(context.getResources().getDrawable(R.drawable.sound_off));
                    sharedPreferences.edit().putBoolean("audio",false).commit();
                }else{
                    imageView.setBackground(context.getResources().getDrawable(R.drawable.sound_on));
                    sharedPreferences.edit().putBoolean("audio",true).commit();
                }

            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog));
        builder.setView(imageView);

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Snackbar.make(imageView, "Sound "+ Boolean.toString(audio), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void onBackPressed(){

        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (navController.getCurrentDestination().getId() == R.id.nav_home) {
            if (alertDialog != null) {
                try {
                    alertDialog.show();
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {

        StartViewModel.saveScore(getApplicationContext());
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_controller_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Sound(MainActivity.this);
                return true;
            case R.id.action_feedback:
                feedBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void feedBack() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

    }

    public void onResume(){
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("values", Context.MODE_PRIVATE);

        if(sharedPreferences.getString("id","").isEmpty()){
            sharedPreferences.edit().putString("id",getPlayerIdGenerator()).commit();
        }

        textViewId.setText(sharedPreferences.getString("id",""));


    }


    public void onDestroy(){
         super.onDestroy();

    }

    private void loadAdMobExit() {
        Log.e("Admob","ExitAdmob");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //builder.setTitle("Confirm exit");
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage("Close the application ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //System.exit(1);
                        finishAffinity();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);


            //alertDialog.getWindow().setGravity(Gravity.BOTTOM);

            AdRequest adRequest = new AdRequest.Builder().build();
            adView = new AdView(this);
            adView.setAdUnitId(getString(R.string.ad_banner_exit_id));
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.loadAd(adRequest);

            Log.e("Admob","test device: "+adRequest.isTestDevice(getApplicationContext()));

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    alertDialog.setView(adView);

                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    //  loadAdStart();
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

        }



}
