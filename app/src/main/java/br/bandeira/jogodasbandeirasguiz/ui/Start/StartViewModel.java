package br.bandeira.jogodasbandeirasguiz.ui.Start;

import android.animation.Animator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import br.bandeira.jogodasbandeirasguiz.Flag;
import br.bandeira.jogodasbandeirasguiz.R;

public class StartViewModel extends ViewModel {

    private Resources res;
    private MutableLiveData<String> mText;
    private static List<Flag> flagList;
    private List<String> options;
    private String[] flagsName;
    private ImageView imageViewFlag;
    private Button button1, button2, button3, button4, button5;
    public static int POSITIONCURRENT = 0;
    public static int SCORE = 0;
    public static int HIT = 0;
    public static int ERROR = 0;
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    private ClipDrawable imageDrawable;
    private Animation animationButton;
    private TextView text_points_value;
    private MediaPlayer playSound;
    private Context context;
    private FloatingActionButton fab;

    public StartViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(Integer.toString(SCORE));
        flagList = new ArrayList();
        options = new ArrayList();


   }

    private void startButtonAnim(Context context){

        animationButton = AnimationUtils.loadAnimation(context, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        animationButton.setInterpolator(interpolator);

    }

    private void chronometerSetup(View root) {

        chronometer = root.findViewById(R.id.chronometer);
        chronometer.setFormat("00:%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 10000) {
                    // chronometer.setBase(SystemClock.elapsedRealtime());

                }
            }
        });

    }

    private void buttonFloatListener(){
        animationButton.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void StartGame(Resources res, View root) {

        startButtonAnim(root.getContext());
        fab = root.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        buttonFloatListener();

        text_points_value = root.findViewById(R.id.text_points_value);
        text_points_value.setText(Integer.toString(SCORE));
        context = root.getContext();

        chronometerSetup(root);

        this.res = res;
        imageDrawable = new ClipDrawable(null, Gravity.CENTER | Gravity.START, ClipDrawable.VERTICAL);
        imageViewFlag = root.findViewById(R.id.imageViewFlag);
        flagsName = res.getStringArray(R.array.flags);
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
        nextFlag();
        startChronometer();
    }

    public String getTime(){
        long millis = SystemClock.elapsedRealtime() - chronometer.getBase();
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }

    private void loadFlags() {

        Flag flag = null;

        if (flagList.size() == 0) {
            for (int i = 0; i < flagsName.length; i++) {
                flag = new Flag();
                flag.setFragName(flagsName[i]);
                flag.setIdImage(idImageFlag[i]);
                flagList.add(flag);
            }

            Collections.shuffle(flagList);
        }


    }

    private void nextFlag() {

        //imageViewFlag.setImageDrawable(res.getDrawable(flagList.get(POSITIONCURRENT).getIdFlag()));
        changeFlagView(flagList.get(POSITIONCURRENT).getIdFlag());
        options.add(flagList.get(POSITIONCURRENT).getFragName());

        int ii;

        do {
            ii = randomOption();

            if (!options.contains(flagList.get(ii).getFragName())) {
                options.add(flagList.get(ii).getFragName());
            }

        } while (options.size() != 5);

        Collections.shuffle(options);

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

        options.clear();

        if (POSITIONCURRENT == flagList.size() - 1) {
            POSITIONCURRENT = 0;
            flagList.clear();
            loadFlags();
        }


    }

    private int randomOption() {
        Random rand = new Random();
        return rand.nextInt(flagList.size() - 1);

    }

    public LiveData<String> getText() {
        return mText;
    }

    public void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
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

    private static final int idImageFlag[] = new int[]{
            R.drawable.afghanistan, R.drawable.albania, R.drawable.algeria, R.drawable.andorra, R.drawable.angola, R.drawable.antigua_and_barbuda,
            R.drawable.argentina, R.drawable.armenia, R.drawable.australia, R.drawable.austria, R.drawable.azerbaijan, R.drawable.bahamas, R.drawable.bahrain, R.drawable.bangladesh, R.drawable.barbados,
            R.drawable.belarus, R.drawable.belgium, R.drawable.belize, R.drawable.benin, R.drawable.bhutan, R.drawable.bolivia, R.drawable.bosnia_and_herzegovina, R.drawable.botswana, R.drawable.brazil,
            R.drawable.brunei, R.drawable.bulgaria, R.drawable.burkina_faso, R.drawable.burundi, R.drawable.cambodia, R.drawable.cameroon, R.drawable.canada, R.drawable.cape_verde, R.drawable.central_african_republic,
            R.drawable.chad, R.drawable.chile, R.drawable.colombia, R.drawable.comoros, R.drawable.costa_rica_state, R.drawable.cote_divoire, R.drawable.croatia, R.drawable.cuba, R.drawable.cyprus, R.drawable.czech_republic,
            R.drawable.democratic_republic_of_congo, R.drawable.denmark, R.drawable.djibouti, R.drawable.dominica, R.drawable.dominican_republic, R.drawable.east_timor, R.drawable.ecuador,
            R.drawable.egypt, R.drawable.el_salvador, R.drawable.equatorial_guinea, R.drawable.eritrea, R.drawable.estonia, R.drawable.ethiopia, R.drawable.federated_states_of_micronesia,
            R.drawable.fiji, R.drawable.finland, R.drawable.france, R.drawable.gabon, R.drawable.gambia, R.drawable.georgia, R.drawable.germany, R.drawable.ghana, R.drawable.greece, R.drawable.grenada, R.drawable.guatemala, R.drawable.guinea,
            R.drawable.guinea_bissau, R.drawable.guyana, R.drawable.haiti, R.drawable.honduras, R.drawable.hungary, R.drawable.iceland, R.drawable.india, R.drawable.indonesia, R.drawable.iran, R.drawable.iraq, R.drawable.ireland, R.drawable.israel,
            R.drawable.italy, R.drawable.jamaica, R.drawable.japan, R.drawable.jordan, R.drawable.kazakhstan, R.drawable.kenya, R.drawable.kiribati, R.drawable.kuwait, R.drawable.kyrgyzstan, R.drawable.laos, R.drawable.latvia, R.drawable.lebanon,
            R.drawable.lesotho, R.drawable.liberia, R.drawable.libya, R.drawable.liechtenstein, R.drawable.lithuania, R.drawable.luxembourg, R.drawable.macedonia, R.drawable.madagascar, R.drawable.malawi, R.drawable.malaysia,
            R.drawable.maldives, R.drawable.mali, R.drawable.malta, R.drawable.marshall_islands, R.drawable.mauritania, R.drawable.mauritius, R.drawable.mexico, R.drawable.moldova, R.drawable.monaco, R.drawable.mongolia,
            R.drawable.montenegro, R.drawable.morocco, R.drawable.mozambique, R.drawable.myanmar, R.drawable.namibia, R.drawable.nauru, R.drawable.nepal, R.drawable.netherlands, R.drawable.new_zealand, R.drawable.nicaragua,
            R.drawable.niger, R.drawable.nigeria, R.drawable.north_korea, R.drawable.norway, R.drawable.oman, R.drawable.pakistan, R.drawable.palau, R.drawable.panama, R.drawable.papua_new_guinea, R.drawable.paraguay, R.drawable.peoples_republic_of_china,
            R.drawable.peru, R.drawable.philippines, R.drawable.poland, R.drawable.portugal, R.drawable.qatar, R.drawable.republic_of_congo, R.drawable.romania, R.drawable.russia, R.drawable.rwanda, R.drawable.saint_kitts_and_nevis,
            R.drawable.saint_lucia, R.drawable.saint_vincent_and_grenadines, R.drawable.samoa, R.drawable.san_marino, R.drawable.sao_tome_and_principe, R.drawable.saudi_arabia,
            R.drawable.senegal, R.drawable.serbia, R.drawable.seychelles, R.drawable.sierra_leone, R.drawable.singapore, R.drawable.slovakia, R.drawable.slovenia, R.drawable.solomon_islands, R.drawable.somalia,
            R.drawable.south_africa, R.drawable.south_korea, R.drawable.south_sudan, R.drawable.spain, R.drawable.sri_lanka, R.drawable.sudan, R.drawable.suriname, R.drawable.swaziland, R.drawable.sweden, R.drawable.switzerland,
            R.drawable.syria, R.drawable.tajikistan, R.drawable.tanzania, R.drawable.thailand, R.drawable.togo, R.drawable.tonga, R.drawable.trinidad_and_tobago, R.drawable.tunisia, R.drawable.turkey, R.drawable.turkmenistan,
            R.drawable.tuvalu, R.drawable.uganda, R.drawable.ukraine, R.drawable.united_arab_emirates, R.drawable.united_kingdom, R.drawable.united_states, R.drawable.uruguay, R.drawable.uzbekistan,
            R.drawable.vanuatu, R.drawable.vatican_city, R.drawable.venezuela, R.drawable.vietnam, R.drawable.yemen, R.drawable.zambia, R.drawable.zimbabwe};


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

            Button button = (Button) v;

            if (button.getText().equals(flagList.get(POSITIONCURRENT).getFragName())) {

                SCORE += 100;
                HIT += 1;

               fab.setBackgroundTintList(ColorStateList.valueOf(res.getColor(R.color.colorBlue)));
               fab.setImageBitmap(textAsBitmap("+100", 24,Color.WHITE));
               playSound = MediaPlayer.create(context, R.raw.sound_nice);

            } else {

                SCORE -= 50;
                ERROR += 1;

                fab.setImageBitmap(textAsBitmap("-50", 24,Color.BLACK));
                fab.setBackgroundTintList(ColorStateList.valueOf(res.getColor(R.color.colorRed)));
                playSound = MediaPlayer.create(context, R.raw.sound_fail);
            }
            playSound.start();
            fab.startAnimation(animationButton);

            text_points_value.setText(Integer.toString(SCORE));
            Log.e("score: ", Integer.toString(SCORE));
            Log.e("hit: ", Integer.toString(HIT));
            Log.e("error: ", Integer.toString(ERROR));
            POSITIONCURRENT++;
            nextFlag();

        }
    }

    private class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }

}