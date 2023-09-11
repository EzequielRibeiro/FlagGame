package br.bandeira.jogodasbandeirasguiz.ui.Start;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;;
import com.google.android.gms.games.PlayGames;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import br.bandeira.jogodasbandeirasguiz.DialogFragmentShowResult;
import br.bandeira.jogodasbandeirasguiz.Flag;
import br.bandeira.jogodasbandeirasguiz.MainActivity;
import br.bandeira.jogodasbandeirasguiz.R;
import br.bandeira.jogodasbandeirasguiz.Score;
import br.bandeira.jogodasbandeirasguiz.ScoreDbHelper;
import static br.bandeira.jogodasbandeirasguiz.ui.Home.HomeFragment.RAWSCORE;
import static br.bandeira.jogodasbandeirasguiz.ui.Home.HomeFragment.TAG;
import static br.bandeira.jogodasbandeirasguiz.ui.Home.HomeFragment.loadScoreOfLeaderBoard;


public class StartViewModel extends ViewModel {

    private Resources res;
    private MutableLiveData<String> mText;
    private static List<Flag> flagList;
    private static List<String> options;
    private String[] flagsName;
    private String[] flagsNameSymbol;
    private ImageView imageViewFlag;
    private static Button button1;
    private static Button button2;
    private static Button button3;
    private Button button4;
    private Button button5;
    private static long pauseOffset;
    private static boolean running = false;
    private ClipDrawable imageDrawable;
    private Animation animationButton;
    private static TextView text_points_value;
    private static RatingBar ratingBar;
    private Context context;
    public static Chronometer CHRONOMETER;
    public static int POSITIONFLAG = 0;
    public static double SCORE = 0;
    public static int HIT = 0;
    public static int ERROR = 0;
    private static int POINTSADD = 100;
    private static int POINTSLESS = 50;
    private static int POINTSPOSITION = 10;
    private FragmentManager fragmentManager;
    private View view;

    private SharedPreferences sharedPreferences;
    private MediaPlayer soundNice, soundFail;

    private static List<Score> itemScore;

    public StartViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(String.valueOf((int)SCORE));
        flagList = new ArrayList();
        options = new ArrayList();



   }

    private void startButtonAnim(Context context){

        animationButton = AnimationUtils.loadAnimation(context, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 60);
        animationButton.setInterpolator(interpolator);
    }

    private void chronometerSetup() {

       // CHRONOMETER = root.findViewById(R.id.chronometer);
        CHRONOMETER.setFormat("00:%s");
        CHRONOMETER.setBase(SystemClock.elapsedRealtime());
        CHRONOMETER.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 10000) {
                    // chronometer.setBase(SystemClock.elapsedRealtime());

                }
            }
        });

    }

    String valuePoint;
    private void buttonFloatListener(){
        animationButton.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                 text_points_value.setText(valuePoint);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                text_points_value.setText(String.valueOf((int)SCORE));
                text_points_value.setTextColor(res.getColor(R.color.colorBlue));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void StartGame(Resources res,View root,FragmentManager fragmentManager) {

        this.fragmentManager = fragmentManager;
        this.view = root;
        startButtonAnim(root.getContext());
        buttonFloatListener();
        ratingBar = root.findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        ratingBar.setMax(5);
        ratingBar.setRating(5);
        text_points_value = root.findViewById(R.id.text_points_value);
        text_points_value.setText(String.valueOf((int)SCORE));
        context = root.getContext();
      sharedPreferences = context.getSharedPreferences("values", Context.MODE_PRIVATE);

        try {

            Uri myUriNice = Uri.parse("android.resource://"+context.getPackageName()+"/raw/sound_nice");
            soundNice = new MediaPlayer();
            soundNice.setAudioStreamType(AudioManager.STREAM_MUSIC);
            soundNice.setDataSource(context, myUriNice);
            soundNice.prepare();

            Uri myUriFail = Uri.parse("android.resource://"+context.getPackageName()+"/raw/sound_fail");
            soundFail = new MediaPlayer();
            soundFail.setAudioStreamType(AudioManager.STREAM_MUSIC);
            soundFail.setDataSource(context,myUriFail);
            soundFail.prepare();



        }catch (IOException | IllegalArgumentException e){
            e.printStackTrace();
        }

        chronometerSetup();

        this.res = res;
        imageDrawable = new ClipDrawable(null, Gravity.CENTER | Gravity.START, ClipDrawable.VERTICAL);
        imageViewFlag = root.findViewById(R.id.imageViewFlag);
        flagsName = res.getStringArray(R.array.flags);
        flagsNameSymbol = res.getStringArray(R.array.symbol);
        ButtonOptionListiner buttonOptionListiner = new ButtonOptionListiner();
        clipDrawable = (ClipDrawable) imageViewFlag.getDrawable();
        clipDrawable.setLevel(10);

        button1 = root.findViewById(R.id.buttonFrag1);
        button1.setOnClickListener(buttonOptionListiner);
        button2 = root.findViewById(R.id.buttonFrag2);
        button2.setOnClickListener(buttonOptionListiner);
        button3 = root.findViewById(R.id.buttonFrag3);
        button3.setOnClickListener(buttonOptionListiner);
        button4 = root.findViewById(R.id.buttonFrag4);
        button4.setOnClickListener(buttonOptionListiner);
        button5 = root.findViewById(R.id.buttonFrag5);
        button5.setOnClickListener(buttonOptionListiner);

        loadFlags();
        startChronometer();



    }

   static public String getTime(){
        long millis = SystemClock.elapsedRealtime() - CHRONOMETER.getBase();
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }

    public  void loadFlags() {

        Flag flag = null;

        if (flagList.size() == 0) {
            for (int i = 0; i < flagsName.length; i++) {
                flag = new Flag();
                flag.setFragName(flagsName[i]);
                flag.setIdImage(idImageFlag[i]);
                flagList.add(flag);
            }

            for (int i = 0; i < flagsNameSymbol.length; i++) {
                flag = new Flag();
                flag.setFragName(flagsNameSymbol[i]);
                flag.setIdImage(idImageSymbol[i]);
                flag.setIsSymbol(true);
                flagList.add(flag);
            }
            Collections.shuffle(flagList);
        }

        nextFlag();
    }

    public static double getScoreValueTime(String time){
        String[] units = time.split(":");
        int hour    = Integer.parseInt(units[0]);
        int minutes = Integer.parseInt(units[1]);
        int seconds = Integer.parseInt(units[2]);
        int duration = (60 * 60 * hour) + (minutes * 60) + seconds;
        double percet = duration * 0.01;

        String value = String.valueOf(hour)+'.'+ minutes+seconds;

       // return (int) (SCORE * percet / 100);

        try{
            return Double.parseDouble(value);
        }catch (NumberFormatException e){
            return 0.0 ;
        }

    }

    public void nextFlag() {

        //increase points
        if(POSITIONFLAG == POINTSPOSITION - 1){
            POINTSADD += 100;
            POINTSLESS += 50;
            POINTSPOSITION += 10;
        }

        //show result game and save score
        if (flagList.size() == POSITIONFLAG || ratingBar.getRating() == 0) {
            //game result save to database
            Log.i(TAG,"Score:"+SCORE);
           // SCORE = SCORE - getScoreValueTime(getTime());
            Log.i(TAG,"Score:"+SCORE);

            if(POSITIONFLAG == HIT){
                showResult(true);
                sendPlayerWinsToFirebase();
            }
            else {
                showResult(false);
            }

            loadFlags();

        }else{

            //imageViewFlag.setImageDrawable(res.getDrawable(flagList.get(POSITIONCURRENT).getIdFlag()));
            changeFlagView(flagList.get(POSITIONFLAG).getIdFlag());
            text_points_value.setText(String.valueOf((int)SCORE));
            int ii;

            if(options.size() == 0) {
                //adiciona na lista de opções o nome da bandeira correta
                options.add(flagList.get(POSITIONFLAG).getFragName());

                //adiciona na lista de opções nomes aleatórios de bandeiras
                do {
                    ii = randomOption();

                    if (!options.contains(flagList.get(ii).getFragName())) {
                        options.add(flagList.get(ii).getFragName());
                    }

                } while (options.size() != 5);

                //embaralha o nome das bandeiras
                Collections.shuffle(options);

            }

            button1.setText(options.get(0));
            // button1.setEnabled(false);
            button2.setText(options.get(1));
            // button2.setEnabled(false);
            button3.setText(options.get(2));
            // button3.setEnabled(false);
            button4.setText(options.get(3));
            // button4.setEnabled(false);
            button5.setText(options.get(4));
            //  button5.setEnabled(false);

        }

        Log.i(MainActivity.TAG,"FLAG: "+ flagList.get(POSITIONFLAG).getFragName());
        Log.i(MainActivity.TAG,"Position: "+ POSITIONFLAG +" HIT: "+HIT+" SIZE: "+ flagList.size() );

    }

    private void sendPlayerWinsToFirebase(){

        SharedPreferences sharedPreferences = context.getSharedPreferences("values", Context.MODE_PRIVATE);
        String idPlayer =  sharedPreferences.getString("id","00");

            FirebaseAnalytics  mFirebaseAnalytics = FirebaseAnalytics.getInstance(view.getContext());

            String country = res.getConfiguration().locale.getDisplayCountry();
            Bundle bundle = new Bundle();
            bundle.putString("IDPLAYER", idPlayer);
            bundle.putString("MAXSCORE", String.valueOf(SCORE));
            bundle.putString("COUNTRY", country);
            bundle.putString("TIME",getTime());
            mFirebaseAnalytics.logEvent("vencedor", bundle);

    }

    public void showResult(boolean win){

        DialogFragmentShowResult dialog = DialogFragmentShowResult.newInstance(win);
        dialog.setCancelable(false);
        dialog.show(fragmentManager,"");

        saveScore(context);


    }


    private int randomOption() {
        Random rand = new Random();
        return rand.nextInt(flagList.size() - 1);

    }

    public LiveData<String> getText() {
        return mText;
    }

    public static void startChronometer() {
        if (!running) {
            CHRONOMETER.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            CHRONOMETER.start();
            running = true;
        }
    }

    public static void pauseChronometer() {
        if (running) {
            CHRONOMETER.stop();
            pauseOffset = SystemClock.elapsedRealtime() - CHRONOMETER.getBase();
            running = false;
        }
    }

    public static void resetChronometer() {
        CHRONOMETER.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    ClipDrawable clipDrawable;
    int CHANGE_LEVEL = 99;
    int LEVEL_MAX = 10000;
    boolean isExpand = false;


    public void changeFlagView(int idFlag) {

        if (imageViewFlag.getDrawable() != null) {
            imageViewFlag.setBackground(imageViewFlag.getDrawable());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imageDrawable.setDrawable(ContextCompat.getDrawable(context,idFlag));
        }else{
            imageDrawable = new ClipDrawable(res.getDrawable(idFlag),Gravity.CENTER,ClipDrawable.VERTICAL);
        }

        imageViewFlag.setImageDrawable(imageDrawable);
        imageDrawable.setLevel(10);
        if (!isExpand) {
            isExpand = true;
            final Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == CHANGE_LEVEL) {
                        int level = imageDrawable.getLevel() + 100;
                        if (level >= LEVEL_MAX) {
                            level = LEVEL_MAX;
                        }
                        imageDrawable.setLevel(level);
                    }
                }
            };
            final CountDownTimer timer = new CountDownTimer(Integer.MAX_VALUE, 10) {
                @Override
                public void onTick(long millisUntilFinished) {

                    if (imageDrawable.getLevel() >= LEVEL_MAX) {
                        this.onFinish();
                    } else {
                        mHandler.sendEmptyMessage(99);
                    }

                }

                @Override
                public void onFinish() {
                    isExpand = false;
                    button1.setEnabled(true);
                    button2.setEnabled(true);
                    button3.setEnabled(true);
                    button4.setEnabled(true);
                    button5.setEnabled(true);

                }
            };
            timer.start();

        }

    }

    public static List<Score> getScoreRecord(Context context){

        itemScore = new ArrayList<>();
        ScoreDbHelper dbHelper = new ScoreDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_DATE,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_TIME,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_FLAGS,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_HIT,
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE + " = ?";
        String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE + " DESC";

        Cursor cursor = db.query(
                ScoreDbHelper.ScoreEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );


        Score score = new Score("FLAGS","HIT","TIME","SCORE");
        itemScore.add(score);

        while(cursor.moveToNext()) {
            score = new Score();
            score.setId(cursor.getLong(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry._ID)));
            score.setDate(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_DATE)));
            score.setTime(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_TIME)));
            score.setFlags(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_FLAGS)));
            score.setHit(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_HIT)));
            score.setScore(cursor.getString(
                    cursor.getColumnIndexOrThrow(ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE)));

            itemScore.add(score);

        }
        cursor.close();
        return itemScore;

    }


    static public void saveScore(Context context){

        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm");
        String currentDateandTime = sdf.format(new Date());

        ScoreDbHelper dbHelper = new ScoreDbHelper(context);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        //player has played ?
        int playerHasPlayed = ERROR + HIT;

        if(playerHasPlayed > 0) {

            values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_DATE, currentDateandTime);
            values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_TIME, StartViewModel.getTime());
            values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_FLAGS, POSITIONFLAG);
            values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_HIT, HIT);
            values.put(ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE,(int)SCORE);

            long newRowId = db.insert(ScoreDbHelper.ScoreEntry.TABLE_NAME, null, values);

            if(newRowId < 0) {
                return;
            }

            Cursor cursor = db.rawQuery("SELECT MAX("+ScoreDbHelper.ScoreEntry.COLUMN_NAME_SCORE+") FROM "+ScoreDbHelper.ScoreEntry.TABLE_NAME, null);
            boolean isValue = cursor.moveToFirst();

            Activity activity = (Activity) context;
            loadScoreOfLeaderBoard(activity);

            if(isValue){
                long max = cursor.getLong(0);
                Log.e(TAG,"MAX: "+ max);

              if(max > 0 && RAWSCORE != 0)//ignore negative number
                if(RAWSCORE < max){
                    PlayGames.getLeaderboardsClient(activity).submitScore(context.getString(R.string.leaderboard_id), max);
                    Log.i(TAG,"Send to Leaderboards");
                }


            }

            POSITIONFLAG = 0;
            POINTSADD = 100;
            POINTSLESS = 50;
            POINTSPOSITION = 10;
            SCORE = 0;
            ERROR = 0;
            HIT   = 0;
            ratingBar.setRating(5);
            pauseChronometer();
            resetChronometer();
           // flagList.clear();

            if (newRowId > 0) {
                Log.i(MainActivity.TAG, "salvo no banco");
           } else {
                Log.i(MainActivity.TAG, "não foi salvo no banco");
            }

        }



    }
	
	 //method to convert your text to image
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private class ButtonOptionListiner implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            button1.setEnabled(false);
            button2.setEnabled(false);
            button3.setEnabled(false);
            button4.setEnabled(false);
            button5.setEnabled(false);
            options.clear();


            Button button = (Button) v;

            if (button.getText().equals(flagList.get(POSITIONFLAG).getFragName())) {

                if(flagList.get(POSITIONFLAG).isSymbol()){

                   // ratingBar.setRating(ratingBar.getRating() + 1);
                    valuePoint = "+".concat(String.valueOf((int)(POINTSADD - (POINTSADD * getScoreValueTime(getTime())))*3));
                    SCORE = SCORE + ((POINTSADD - (POINTSADD * getScoreValueTime(getTime())))*3);
                }else{
                    valuePoint = "+".concat(String.valueOf((int)(POINTSADD - (POINTSADD * getScoreValueTime(getTime())))));
                    SCORE = SCORE + (POINTSADD - (POINTSADD * getScoreValueTime(getTime())));
                }

               HIT += 1;
               text_points_value.setTextColor(res.getColor(R.color.colorGreen));

                if(sharedPreferences.getBoolean("audio",true)){

                    soundNice.seekTo(0);
                    soundNice.start();

                }


            } else {
                ratingBar.setRating(ratingBar.getRating() - 1);
                SCORE = SCORE - (POINTSLESS + (POINTSLESS * getScoreValueTime(getTime()))) ;
                ERROR += 1;
                text_points_value.setTextColor(res.getColor(R.color.colorRed));
                valuePoint = "-".concat(String.valueOf((int)(POINTSLESS + (POINTSLESS * getScoreValueTime(getTime())))));

                if(sharedPreferences.getBoolean("audio",true)){
                    soundFail.start();
                }

            }

            text_points_value.startAnimation(animationButton);


            POSITIONFLAG++;
            nextFlag();

        }
    }

    private class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude;
        private double mFrequency;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }
	
    private static final int idImageFlag[] = new int[]{
        R.drawable.afghanistan,
        R.drawable.albania,
        R.drawable.algeria,
        R.drawable.andorra,
        R.drawable.angola,
        R.drawable.antigua_and_barbuda,
        R.drawable.argentina,
        R.drawable.armenia,
        R.drawable.australia,
        R.drawable.austria,
        R.drawable.azerbaijan,
        R.drawable.bahamas,
        R.drawable.bahrain,
        R.drawable.bangladesh,
        R.drawable.barbados,
        R.drawable.belarus,
        R.drawable.belgium,
        R.drawable.belize,
        R.drawable.benin,
        R.drawable.bhutan,
        R.drawable.bolivia,
        R.drawable.bosnia_and_herzegovina,
        R.drawable.botswana,
        R.drawable.brazil,
        R.drawable.brunei,
        R.drawable.bulgaria,
        R.drawable.burkina_faso,
        R.drawable.burundi,
        R.drawable.cambodia,
        R.drawable.cameroon,
        R.drawable.canada,
        R.drawable.cape_verde,
        R.drawable.central_african_republic,
        R.drawable.chad,
        R.drawable.chile,
        R.drawable.china,
        R.drawable.colombia,
        R.drawable.comoros,
        R.drawable.costa_rica_state,
        R.drawable.cote_divoire,
        R.drawable.croatia,
        R.drawable.cuba,
        R.drawable.cyprus,
        R.drawable.czech_republic,
        R.drawable.denmark,
        R.drawable.djibouti,
        R.drawable.dominica,
        R.drawable.dominican_republic,
        R.drawable.east_timor,
        R.drawable.ecuador,
        R.drawable.egypt,
        R.drawable.el_salvador,
        R.drawable.equatorial_guinea,
        R.drawable.eritrea,
        R.drawable.estonia,
        R.drawable.ethiopia,
        R.drawable.federated_states_of_micronesia,
        R.drawable.fiji,
        R.drawable.finland,
        R.drawable.france,
        R.drawable.gabon,
        R.drawable.gambia,
        R.drawable.georgia,
        R.drawable.germany,
        R.drawable.ghana,
        R.drawable.greece,
        R.drawable.grenada,
        R.drawable.guatemala,
        R.drawable.guinea,
        R.drawable.guinea_bissau,
        R.drawable.guyana,
        R.drawable.haiti,
        R.drawable.honduras,
        R.drawable.hungary,
        R.drawable.iceland,
        R.drawable.india,
        R.drawable.indonesia,
        R.drawable.iran,
        R.drawable.iraq,
        R.drawable.ireland,
        R.drawable.israel,
        R.drawable.italy,
        R.drawable.jamaica,
        R.drawable.japan,
        R.drawable.jordan,
        R.drawable.kazakhstan,
        R.drawable.kenya,
        R.drawable.kiribati,
        R.drawable.kosovo,
        R.drawable.kuwait,
        R.drawable.kyrgyzstan,
        R.drawable.laos,
        R.drawable.latvia,
        R.drawable.lebanon,
        R.drawable.lesotho,
        R.drawable.liberia,
        R.drawable.libya,
        R.drawable.liechtenstein,
        R.drawable.lithuania,
        R.drawable.luxembourg,
        R.drawable.macedonia,
        R.drawable.madagascar,
        R.drawable.malawi,
        R.drawable.malaysia,
        R.drawable.maldives,
        R.drawable.mali,
        R.drawable.malta,
        R.drawable.marshall_islands,
        R.drawable.mauritania,
        R.drawable.mauritius,
        R.drawable.mexico,
        R.drawable.micronesia,
        R.drawable.moldova,
        R.drawable.monaco,
        R.drawable.mongolia,
        R.drawable.montenegro,
        R.drawable.morocco,
        R.drawable.mozambique,
        R.drawable.myanmar,
        R.drawable.namibia,
        R.drawable.nauru,
        R.drawable.nepal,
        R.drawable.netherlands,
        R.drawable.new_zealand,
        R.drawable.nicaragua,
        R.drawable.niger,
        R.drawable.nigeria,
        R.drawable.niue,
        R.drawable.north_korea,
        R.drawable.norway,
        R.drawable.oman,
        R.drawable.pakistan,
        R.drawable.palau,
        R.drawable.panama,
        R.drawable.papua_new_guinea,
        R.drawable.paraguay,
        R.drawable.peru,
        R.drawable.philippines,
        R.drawable.poland,
        R.drawable.portugal,
        R.drawable.qatar,
        R.drawable.republic_of_congo,
        R.drawable.romania,
        R.drawable.russia,
        R.drawable.rwanda,
        R.drawable.saint_kitts_and_nevis,
        R.drawable.saint_lucia,
        R.drawable.saint_vincent_and_grenadines,
        R.drawable.samoa,
        R.drawable.san_marino,
        R.drawable.sao_tome_and_principe,
        R.drawable.saudi_arabia,
        R.drawable.senegal,
        R.drawable.serbia,
        R.drawable.seychelles,
        R.drawable.sierra_leone,
        R.drawable.singapore,
        R.drawable.slovakia,
        R.drawable.slovenia,
        R.drawable.solomon_islands,
        R.drawable.somalia,
        R.drawable.south_africa,
        R.drawable.south_korea,
        R.drawable.south_sudan,
        R.drawable.spain,
        R.drawable.sri_lanka,
        R.drawable.sudan,
        R.drawable.suriname,
        R.drawable.swaziland,
        R.drawable.sweden,
        R.drawable.switzerland,
        R.drawable.syria,
        R.drawable.taiwan,
        R.drawable.tajikistan,
        R.drawable.tanzania,
        R.drawable.thailand,
        R.drawable.togo,
        R.drawable.tonga,
        R.drawable.trinidad_and_tobago,
        R.drawable.tunisia,
        R.drawable.turkey,
        R.drawable.turkmenistan,
        R.drawable.tuvalu,
        R.drawable.uganda,
        R.drawable.ukraine,
        R.drawable.united_arab_emirates,
        R.drawable.united_kingdom,
        R.drawable.united_states,
        R.drawable.uruguay,
        R.drawable.uzbekistan,
        R.drawable.vanuatu,
        R.drawable.vatican_city,
        R.drawable.venezuela,
        R.drawable.vietnam,
        R.drawable.yemen,
        R.drawable.zambia,
        R.drawable.zimbabwe};

   private static final int[] idImageSymbol = new int[] {
        R.drawable.afghanistan_symbol,
        R.drawable.argentina_symbol,
        R.drawable.barbados_symbol,
        R.drawable.belize_symbol,
        R.drawable.bhutan_symbol,
        R.drawable.iran_symbol,
        R.drawable.israel_symbol,
        R.drawable.mexico_symbol,
        R.drawable.mongolia_symbol,
        R.drawable.montenegro_symbol,
        R.drawable.mozambique_symbol,
        R.drawable.portugal_symbol,
        R.drawable.serbia_symbol,
        R.drawable.south_korea_symbol,
        R.drawable.sri_lanka_symbol,
        R.drawable.uganda_symbol,
        R.drawable.vatican_city_symbol};

   

}