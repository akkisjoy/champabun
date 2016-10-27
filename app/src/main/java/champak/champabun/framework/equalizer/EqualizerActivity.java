package champak.champabun.framework.equalizer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.media.AudioManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import champak.champabun.R;
import champak.champabun.business.utilities.utilMethod.PlayMeePreferences;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.framework.service.Music_service;
import champak.champabun.view.activity.BaseActivity;
//** sixsquare **//

public class EqualizerActivity extends BaseActivity {
    public int prevPresetIndex;
    public Equalizer mEqualizer;
    public DatabaseHandler db;
    public RoundKnobButton rv2;
    public Button eq_status;
    public PlayMeePreferences prefs;
    public ArrayAdapter<String> dataAdapter;
    public ArrayList<String> spinnerList;
    public Spinner spinner;
    public Paint paint1, paint2, paint3, paint4, paint5, midcolor, circle1, circle2, circle3, circle4, circle5,
            glowcolor1, glowcolor2, glowcolor3, glowcolor4, glowcolor5;
    Singleton m_Inst = Singleton.getInstance();
    short band;
    Shader shaderTop, shaderBottom, shaderNormal;
    Boolean eq;
    View eq_wrapping;
    AudioManager maudiomanager;
    private int Xvalue;
    private int Yvalue;
    private int Y1;
    private int Y2;
    private int Y3;
    private int Y4;
    private int Y5;
    private int X1;
    private int X2;
    private int X3;
    private int X4;
    private int X5;
    private int I1;
    private int I2;
    private int I3;
    private int I4;
    private int I5;
    private int D1;
    private int D2;
    private int D3;
    private int D4;
    private int D5;
    private int DeviceHeight;
    private int DeviceWidth;
    private int Mid;
    private int W1;
    private int W2;
    private int W3;
    private int W4;
    private int W5;
    private int W6;
    private int W7;
    private int W8;
    private int W9;
    private int W10;
    private int gap;
    private int Mgap1;
    private int Mgap2;
    private int Mgap3;
    private int Mgap4;
    private int strokewidth;
    private int count;
    private int btn_width;
    private int per1;
    private int per2;
    private int per3;
    private int buttonvaluecount;
    private int textsize;
    private int Boder_top;
    private int Boder_bottom;
    private int C1;
    private int C2;
    private int C3;
    private int C4;
    private int C5;
    private int stator1;
    private int stator2;
    private int stator3;
    private int volume;
    private int presetcount;
    private int l1;
    private int l2;
    private int l3;
    private int l4;
    private int l5;
    private int h1;
    private int h2;
    private int h3;
    private int h4;
    private int h5;
    private int mid;
    private int spinnerposition;
    private int curPresetIndex;
    private int Y;
    private int setfocus;
    private Virtualizer virtualizer;
    private BassBoost bassBoost;
    private Dialog dialog;
    private Bitmap bitmap;
    private ImageView drawingImageView, ivBack1, ivBack2, ivBack3;
    private Canvas canvas;
    private ImageView backPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(this);

        eq_status = (Button) findViewById(R.id.equalizerOnOff);
        eq_wrapping = findViewById(R.id.equalizerOnOffwrapping);
        eq_status.getBackground().setColorFilter(Color.parseColor("#666666"), PorterDuff.Mode.SRC_ATOP);
        spinner = (Spinner) findViewById(R.id.saved_preset);

        backPager = (ImageView) findViewById(R.id.backPager);
        backPager.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);

        prefs = new PlayMeePreferences(EqualizerActivity.this);
        eq = prefs.IsEqualizerOn();
        initializeEqualizers();
        checkEq();

        // audio manager to change volume
        maudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        percentcalcVolume();
        percentcalcBass();
        percentcalcVirtualizer();
        Singleton.theEqualizer.setEnabled(eq);
        eq_wrapping.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                eq = !eq;
                prefs.SaveEqualizerState(eq);
                checkEq();

            }
        });
        SetupSpinner();
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(size);


        DeviceHeight = size.y;
        DeviceWidth = size.x;
        Boder_top = DeviceHeight * 20 / 100;
        Boder_bottom = DeviceHeight * 70 / 100;
        Mid = (Boder_top + ((Boder_bottom - Boder_top) / 2));
        btn_width = DeviceWidth / 3;
        Log.d(" dh" + Boder_bottom, " mid" + Boder_top);
        if (DeviceWidth <= 480) {
            gap = 10;
            strokewidth = 7;
            textsize = 15;
        } else if (DeviceWidth > 480 && DeviceWidth <= 640) {
            gap = 15;
            strokewidth = 8;
            textsize = 17;
        } else if (DeviceWidth > 640 && DeviceWidth <= 800) {
            gap = 20;
            strokewidth = 9;
            textsize = 20;
        } else if (DeviceWidth > 800 && DeviceWidth <= 960) {
            gap = 30;
            strokewidth = 10;
            textsize = 22;
        } else if (DeviceWidth > 960 && DeviceWidth <= 1120) {
            gap = 35;
            strokewidth = 12;
            textsize = 24;
        } else if (DeviceWidth > 1120 && DeviceWidth <= 1280) {
            gap = 40;
            strokewidth = 15;
            textsize = 25;
        } else if (DeviceWidth > 1280 && DeviceWidth <= 1440) {
            gap = 45;
            strokewidth = 15;
            textsize = 26;
        }
        W1 = gap;
        W2 = (DeviceWidth * 2 / 10) - gap;
        W3 = (DeviceWidth * 2 / 10) + gap;
        W4 = (DeviceWidth * 4 / 10) - gap;
        W5 = (DeviceWidth * 4 / 10) + gap;
        W6 = (DeviceWidth * 6 / 10) - gap;
        W7 = (DeviceWidth * 6 / 10) + gap;
        W8 = (DeviceWidth * 8 / 10) - gap;
        W9 = (DeviceWidth * 8 / 10) + gap;
        W10 = DeviceWidth - gap;
        Mgap1 = W2 + ((W3 - W2) / 2);
        Mgap2 = W4 + ((W5 - W4) / 2);
        Mgap3 = W6 + ((W7 - W6) / 2);
        Mgap4 = W8 + ((W9 - W8) / 2);
        X1 = W1 + ((W2 - W1) / 2);
        X2 = W3 + ((W4 - W3) / 2);
        X3 = W5 + ((W6 - W5) / 2);
        X4 = W7 + ((W8 - W7) / 2);
        X5 = W9 + (W10 - W9) / 2;

        Log.d("Insert: ", "Inserting ..");
        buttonvaluecount = db.getrowCount();
        Log.d("buttonvalue" + buttonvaluecount, "ok");
        if (buttonvaluecount == 0) {
            db.addvalue(new Buttonvalues(0, 0, 0, R.drawable.stator1, R.drawable.stator1, R.drawable.stator1));
            List<Buttonvalues> buttonvalues = db.getAllValues();
            for (Buttonvalues cn1 : buttonvalues) {
                String logvalue = "ids" + cn1.getIDS() + cn1.getBass() + cn1.getVol() + cn1.getVirtualizer()
                        + cn1.getB1() + cn1.getB2() + cn1.getB3();
                Log.d("Value: ", logvalue);
                per1 = cn1.getBass();
                per2 = cn1.getVol();
                per3 = cn1.getVirtualizer();
                stator1 = cn1.getB1();
                stator2 = cn1.getB2();
                stator3 = cn1.getB3();
            }
        }
        List<Buttonvalues> buttonvalues = db.getAllValues();
        for (Buttonvalues cn1 : buttonvalues) {
            String logvalue = "ids" + cn1.getIDS() + cn1.getBass() + cn1.getVol() + cn1.getVirtualizer() + "one"
                    + cn1.getB1() + "two" + cn1.getB2() + "three" + cn1.getB3();
            Log.d("buttonValue: ", logvalue);
            per1 = cn1.getBass();
            per3 = cn1.getVirtualizer();
            stator1 = cn1.getB1();
            stator3 = cn1.getB3();
        }
        count = db.getContactsCount();
        if (count == 0) {
            db.addPath(new Pathvalues(Mid, Mid, Mid, Mid, Mid));
        }
        Log.d("Reading: ", "Reading all contacts..");
        List<Pathvalues> pathvalues = db.getAllContacts();
        for (Pathvalues cn : pathvalues) {
            String log = "id" + cn.getID() + cn.getY1() + cn.getY2() + cn.getY3() + cn.getY4() + cn.getY5();
            Log.d("Value: ", log);
            Y1 = cn.getY1();
            Y2 = cn.getY2();
            Y3 = cn.getY3();
            Y4 = cn.getY4();
            Y5 = cn.getY5();
            pathcolor();
            if (Y1 > Mid) {
                D1 = ((Y1 - Mid) * 30) / 100;
                I1 = Mid + D1;
                C1 = Y1 - (((Y1 - Mid) * 20) / 100);
                paint1.setShader(shaderBottom);
                circle1.setColor(getResources().getColor(R.color.graphbottomcolor));
                glowcolor1.setColor(getResources().getColor(R.color.glowcolorbottom));
            } else if (Y1 < Mid) {
                D1 = ((Mid - Y1) * 30) / 100;
                I1 = Mid - D1;
                C1 = Y1 + (((Mid - Y1) * 20) / 100);
                paint1.setShader(shaderTop);
                circle1.setColor(getResources().getColor(R.color.graphtopcolor));
                glowcolor1.setColor(getResources().getColor(R.color.glowcolortop));
            } else if (Y1 == Mid) {
                I1 = Mid;
                C1 = Y1;
                paint1.setShader(shaderNormal);
                circle1.setColor(getResources().getColor(R.color.graphnormalcolor));
                glowcolor1.setColor(getResources().getColor(R.color.glowcolornormal));
            }
            if (Y2 > Mid) {
                D2 = ((Y2 - Mid) * 30) / 100;
                I2 = Mid + D2;
                C2 = Y2 - (((Y2 - Mid) * 20) / 100);
                paint2.setShader(shaderBottom);
                circle2.setColor(getResources().getColor(R.color.graphbottomcolor));
                glowcolor2.setColor(getResources().getColor(R.color.glowcolorbottom));
            } else if (Y2 < Mid) {
                D2 = ((Mid - Y2) * 30) / 100;
                I2 = Mid - D2;
                C2 = Y2 + (((Mid - Y2) * 20) / 100);
                paint2.setShader(shaderTop);
                circle2.setColor(getResources().getColor(R.color.graphtopcolor));
                glowcolor2.setColor(getResources().getColor(R.color.glowcolortop));
            } else if (Y2 == Mid) {
                I2 = Mid;
                C2 = Y2;
                paint2.setShader(shaderNormal);
                circle2.setColor(getResources().getColor(R.color.graphnormalcolor));
                glowcolor2.setColor(getResources().getColor(R.color.glowcolornormal));
            }
            if (Y3 > Mid) {
                D3 = ((Y3 - Mid) * 30) / 100;
                I3 = Mid + D3;
                C3 = Y3 - (((Y3 - Mid) * 20) / 100);
                paint3.setShader(shaderBottom);
                circle3.setColor(getResources().getColor(R.color.graphbottomcolor));
                glowcolor3.setColor(getResources().getColor(R.color.glowcolorbottom));
            } else if (Y3 < Mid) {
                D3 = ((Mid - Y3) * 30) / 100;
                I3 = Mid - D3;
                C3 = Y3 + (((Mid - Y3) * 20) / 100);
                paint3.setShader(shaderTop);
                circle3.setColor(getResources().getColor(R.color.graphtopcolor));
                glowcolor3.setColor(getResources().getColor(R.color.glowcolortop));
            } else if (Y3 == Mid) {
                I3 = Mid;
                C3 = Y3;
                paint3.setShader(shaderNormal);
                circle3.setColor(getResources().getColor(R.color.graphnormalcolor));
                glowcolor3.setColor(getResources().getColor(R.color.glowcolornormal));
            }
            if (Y4 > Mid) {
                D4 = ((Y4 - Mid) * 30) / 100;
                I4 = Mid + D4;
                C4 = Y4 - (((Y4 - Mid) * 20) / 100);
                paint4.setShader(shaderBottom);
                circle4.setColor(getResources().getColor(R.color.graphbottomcolor));
                glowcolor4.setColor(getResources().getColor(R.color.glowcolorbottom));
            } else if (Y4 < Mid) {
                D4 = ((Mid - Y4) * 30) / 100;
                I4 = Mid - D4;
                C4 = Y4 + (((Mid - Y4) * 20) / 100);
                paint4.setShader(shaderTop);
                circle4.setColor(getResources().getColor(R.color.graphtopcolor));
                glowcolor4.setColor(getResources().getColor(R.color.glowcolortop));
            } else if (Y4 == Mid) {
                I4 = Mid;
                C4 = Y4;
                paint4.setShader(shaderNormal);
                circle4.setColor(getResources().getColor(R.color.graphnormalcolor));
                glowcolor4.setColor(getResources().getColor(R.color.glowcolornormal));
            }
            if (Y5 > Mid) {
                D5 = ((Y5 - Mid) * 30) / 100;
                I5 = Mid + D5;
                C5 = Y5 - (((Y5 - Mid) * 20) / 100);
                paint5.setShader(shaderBottom);
                circle5.setColor(getResources().getColor(R.color.graphbottomcolor));
                glowcolor5.setColor(getResources().getColor(R.color.glowcolorbottom));
            } else if (Y5 < Mid) {
                D5 = ((Mid - Y5) * 30) / 100;
                I5 = Mid - D5;
                C5 = Y5 + (((Mid - Y5) * 20) / 100);
                paint5.setShader(shaderTop);
                circle5.setColor(getResources().getColor(R.color.graphtopcolor));
                glowcolor5.setColor(getResources().getColor(R.color.glowcolortop));
            } else if (Y5 == Mid) {
                I5 = Mid;
                C5 = Y5;
                paint5.setShader(shaderNormal);
                circle5.setColor(getResources().getColor(R.color.graphnormalcolor));
                glowcolor5.setColor(getResources().getColor(R.color.glowcolornormal));
            }
            drawingImageView = (ImageView) this.findViewById(R.id.image);
            createbitmap();
            drawpath();
        }
        m_Inst.InitGUIFrame(this);
        RelativeLayout panel1 = (RelativeLayout) findViewById(R.id.button1);
        panel1.getLayoutParams().width = btn_width;
        panel1.getLayoutParams().height = btn_width;
        RelativeLayout panel2 = (RelativeLayout) findViewById(R.id.button2);
        panel2.getLayoutParams().width = btn_width;
        panel2.getLayoutParams().height = btn_width;
        RelativeLayout panel3 = (RelativeLayout) findViewById(R.id.button3);
        panel3.getLayoutParams().width = btn_width;
        panel3.getLayoutParams().height = btn_width;
        RoundKnobButton rv1 = new RoundKnobButton(this, R.drawable.rotoroff, R.drawable.rotoron, R.drawable.rotoroff,
                m_Inst.Scale(215), m_Inst.Scale(215));
        rv2 = new RoundKnobButton(this, R.drawable.rotoroff, R.drawable.rotoron, R.drawable.rotoroff, m_Inst.Scale(215),
                m_Inst.Scale(215));
        RoundKnobButton rv3 = new RoundKnobButton(this, R.drawable.rotoroff, R.drawable.rotoron, R.drawable.rotoroff,
                m_Inst.Scale(215), m_Inst.Scale(215));
        panel1.addView(rv1);
        panel2.addView(rv2);
        panel3.addView(rv3);

        // image bg pic selector
        ivBack1 = new ImageView(getApplicationContext());
        ivBack1.setImageResource(stator1);
        ivBack2 = new ImageView(getApplicationContext());
        ivBack3 = new ImageView(getApplicationContext());
        ivBack3.setImageResource(stator3);

        RelativeLayout.LayoutParams lp_ivBack = new RelativeLayout.LayoutParams(215, 215);
        lp_ivBack.addRule(RelativeLayout.CENTER_IN_PARENT);
        panel1.addView(ivBack1, lp_ivBack);
        panel2.addView(ivBack2, lp_ivBack);
        panel3.addView(ivBack3, lp_ivBack);
        // end current work

        percentcalcBass();
        percentcalcVirtualizer();
        statorselectorVolume();
        statorselectorBassBoost();
        statorselectorVirtualizer();
        ivBack2.setImageResource(stator2);
        rv2.setRotorPercentage(per2);
        TextView voltext = (TextView) findViewById(R.id.voltext);
        voltext.getLayoutParams().width = btn_width;
        voltext.setPadding(0, 5, 0, 20);
        voltext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        TextView basstext = (TextView) findViewById(R.id.basstext);
        basstext.getLayoutParams().width = btn_width;
        basstext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        basstext.setPadding(0, 5, 0, 20);
        TextView virtualizertext = (TextView) findViewById(R.id.virtualizertext);
        virtualizertext.getLayoutParams().width = btn_width;
        virtualizertext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        virtualizertext.setPadding(0, 5, 0, 20);
        panel1.getLayoutParams().width = btn_width;
        panel1.setPadding(0, 5, 0, 0);
        panel2.setPadding(0, 5, 0, 0);
        panel3.setPadding(0, 5, 0, 0);

        rv1.setRotorPercentage(per1);
        rv1.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            @Override
            public void onRotate(int percentage, int statorvalue) {
                per1 = percentage;
                stator1 = statorvalue;
                db.updateButtonValue(new Buttonvalues(1, per1, per2, per3, stator1, stator2, stator3));
                setBassBoost(bassBoost, percentage);
                Log.d(" " + percentage, " " + stator1);
                ivBack1.setImageResource(stator1);
                //TODO bass
            }
        });
        rv2.setRotorPercentage(per2);
        rv2.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            @Override
            public void onRotate(int percentage, int statorvalue) {
                per2 = percentage;
                stator2 = statorvalue;
                db.updateButtonValue(new Buttonvalues(1, per1, per2, per3, stator1, stator2, stator3));
                maudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                volumecalc();
                maudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                int volumevalue = maudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                ivBack2.setImageResource(stator2);
                Log.d("volume" + volume, " is  " + volumevalue);
            }
        });
        rv3.setRotorPercentage(per3);
        rv3.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            @Override
            public void onRotate(int percentage, int statorvalue) {
                per3 = percentage;
                stator3 = statorvalue;
                db.updateButtonValue(new Buttonvalues(1, per1, per2, per3, stator1, stator2, stator3));
                setVirtualizer(virtualizer, percentage);
                ivBack3.setImageResource(stator3);
            }
        });
    }

    private void percentcalcBass() {
        int i = bassBoost.getRoundedStrength();
        per1 = i / 10;
    }

    private void checkEq() {
        if (eq) {
            Singleton.theEqualizer.setEnabled(true);
            short x = band;
            int yx = Y;
            List<Pathvalues> pathvalues = db.getAllContacts();
            for (Pathvalues cn : pathvalues) {
                Y1 = cn.getY1();
                Y2 = cn.getY2();
                Y3 = cn.getY3();
                Y4 = cn.getY4();
                Y5 = cn.getY5();
            }
            for (band = 0; band < 5; band++) {
                if (band == 0) Y = Y1;
                else if (band == 1) Y = Y2;
                else if (band == 2) Y = Y3;
                else if (band == 3) Y = Y4;
                else if (band == 4) Y = Y5;
                setupEqualizerFxAndUI();
            }
            band = x;
            Y = yx;
            setBassBoost(bassBoost, per1);
            setVirtualizer(virtualizer, per3);
            eq_status.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
        } else {
            Singleton.theEqualizer.setEnabled(false);
            setBassBoostOff(bassBoost);
            setVirtualizerOff(virtualizer);
            eq_status.getBackground().setColorFilter(Color.parseColor("#666666"), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void percentcalcVirtualizer() {
        int i = virtualizer.getRoundedStrength();
        per3 = i / 10;
    }

    private void percentcalcVolume() {
        int i = maudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d((new StringBuilder("Media player")).append(per2).toString(),
                (new StringBuilder("system volume is... ")).append(i).toString());
        if (i == 0) {
            per2 = 0;
        } else if (i == 1) {
            per2 = 7;
        } else if (i == 2) {
            per2 = 15;
        } else if (i == 3) {
            per2 = 21;
        } else if (i == 4) {
            per2 = 28;
        } else if (i == 5) {
            per2 = 34;
        } else if (i == 6) {
            per2 = 41;
        } else if (i == 7) {
            per2 = 48;
        } else if (i == 8) {
            per2 = 54;
        } else if (i == 9) {
            per2 = 61;
        } else if (i == 10) {
            per2 = 68;
        } else if (i == 11) {
            per2 = 74;
        } else if (i == 12) {
            per2 = 81;
        } else if (i == 13) {
            per2 = 87;
        } else if (i == 14) {
            per2 = 94;
        } else if (i == 15) {
            per2 = 99;
        }
    }

    private void pathcolor() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        shaderTop = new RadialGradient(width / 2, height / 2, 2 * height / 5, getResources().getColor(R.color.graphnormalcolor), getResources().getColor(R.color.graphtopcolor), TileMode.CLAMP);
        shaderBottom = new RadialGradient(width / 2, height / 2, 2 * height / 5, getResources().getColor(R.color.graphnormalcolor), getResources().getColor(R.color.graphbottomcolor), TileMode.CLAMP);
        shaderNormal = new RadialGradient(width / 2, height / 2, 2 * height / 5, getResources().getColor(R.color.graphnormalcolor), getResources().getColor(R.color.graphbottomcolor), TileMode.CLAMP);

        paint1 = new Paint();
        paint1.setStrokeWidth(strokewidth);
        paint1.setStyle(Paint.Style.STROKE);
        // paint2
        paint2 = new Paint();
        paint2.setStrokeWidth(strokewidth);
        paint2.setStyle(Paint.Style.STROKE);
        // paint3
        paint3 = new Paint();
        paint3.setStrokeWidth(strokewidth);
        paint3.setStyle(Paint.Style.STROKE);
        // paint4
        paint4 = new Paint();
        paint4.setStrokeWidth(strokewidth);
        paint4.setStyle(Paint.Style.STROKE);
        // paint5
        paint5 = new Paint();
        paint5.setStrokeWidth(strokewidth);
        paint5.setStyle(Paint.Style.STROKE);
        // circle1
        circle1 = new Paint();
        circle1.setStrokeWidth(2);
        circle1.setStyle(Paint.Style.FILL);
        // circle2
        circle2 = new Paint();
        circle2.setStrokeWidth(2);
        circle2.setStyle(Paint.Style.FILL);
        // circle3
        circle3 = new Paint();
        circle3.setStrokeWidth(2);
        circle3.setStyle(Paint.Style.FILL);
        // circle4
        circle4 = new Paint();
        circle4.setStrokeWidth(2);
        circle4.setStyle(Paint.Style.FILL);
        // circle5
        circle5 = new Paint();
        circle5.setStrokeWidth(2);
        circle5.setStyle(Paint.Style.FILL);
        // middlepath
        midcolor = new Paint();
        midcolor.setStrokeWidth(strokewidth);
        midcolor.setStyle(Paint.Style.STROKE);
        midcolor.setColor(getResources().getColor(R.color.graphnormalcolor));
        // Glow color1
        glowcolor1 = new Paint();
        glowcolor1.setStrokeWidth(strokewidth);
        glowcolor1.setStyle(Paint.Style.STROKE);
        // Glow color2
        glowcolor2 = new Paint();
        glowcolor2.setStrokeWidth(strokewidth);
        glowcolor2.setStyle(Paint.Style.STROKE);
        // Glow color3
        glowcolor3 = new Paint();
        glowcolor3.setStrokeWidth(strokewidth);
        glowcolor3.setStyle(Paint.Style.STROKE);
        // Glow color4
        glowcolor4 = new Paint();
        glowcolor4.setStrokeWidth(strokewidth);
        glowcolor4.setStyle(Paint.Style.STROKE);
        // Glow color5
        glowcolor5 = new Paint();
        glowcolor5.setStrokeWidth(strokewidth);
        glowcolor5.setStyle(Paint.Style.STROKE);

    }

    private void SetupSpinner() {
        adapterlist();
        dataAdapter = new ArrayAdapter<>(this, R.layout.simplerow, spinnerList);
        dataAdapter.setDropDownViewResource(R.layout.simplerow);
        spinner.setAdapter(dataAdapter);
        spinnerposition = m_Inst.getCurPresetIndex(EqualizerActivity.this);
        spinner.setSelection(spinnerposition);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                spinnerposition = position;
                m_Inst.savePresetIndex(spinnerposition, EqualizerActivity.this);
                prevPresetIndex = position;
                h1 = DeviceHeight * 40 / 100;
                h2 = DeviceHeight * 35 / 100;
                h3 = DeviceHeight * 30 / 100;
                h4 = DeviceHeight * 25 / 100;
                h5 = DeviceHeight * 20 / 100;
                mid = DeviceHeight * 45 / 100;
                l1 = DeviceHeight * 50 / 100;
                l2 = DeviceHeight * 55 / 100;
                l3 = DeviceHeight * 60 / 100;
                l4 = DeviceHeight * 65 / 100;
                l5 = DeviceHeight * 70 / 100;
                if (spinnerposition == 0) {
                    setupEqualizerFxAndUI();
                } else if (spinnerposition == 1) {
                    ShowNewPresetDialog();
                } else if (spinnerposition == 2) {
                    Singleton.theEqualizer.usePreset((short) 0);
                    Y1 = h1;
                    Y2 = mid;
                    Y3 = mid;
                    Y4 = mid;
                    Y5 = h1;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 3) {
                    Singleton.theEqualizer.usePreset((short) 1);
                    Y1 = h2;
                    Y2 = h1;
                    Y3 = l1;
                    Y4 = h2;
                    Y5 = h2;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 4) {
                    Singleton.theEqualizer.usePreset((short) 2);
                    Y1 = h2;
                    Y2 = mid;
                    Y3 = h1;
                    Y4 = h2;
                    Y5 = h1;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 5) {
                    Singleton.theEqualizer.usePreset((short) 3);
                    Y1 = mid;
                    Y2 = mid;
                    Y3 = mid;
                    Y4 = mid;
                    Y5 = mid;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 6) {
                    Singleton.theEqualizer.usePreset((short) 4);
                    Y1 = h1;
                    Y2 = mid;
                    Y3 = mid;
                    Y4 = h1;
                    Y5 = l1;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 7) {
                    Singleton.theEqualizer.usePreset((short) 5);
                    Y1 = h2;
                    Y2 = h1;
                    Y3 = h3;
                    Y4 = h1;
                    Y5 = mid;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 8) {
                    Singleton.theEqualizer.usePreset((short) 6);
                    Y1 = h2;
                    Y2 = h1;
                    Y3 = mid;
                    Y4 = h1;
                    Y5 = h1;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 9) {
                    Singleton.theEqualizer.usePreset((short) 7);
                    Y1 = h2;
                    Y2 = h1;
                    Y3 = l1;
                    Y4 = h1;
                    Y5 = h2;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 10) {
                    Singleton.theEqualizer.usePreset((short) 8);
                    Y1 = l1;
                    Y2 = h1;
                    Y3 = h2;
                    Y4 = h1;
                    Y5 = l1;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition == 11) {
                    Singleton.theEqualizer.usePreset((short) 9);
                    Y1 = h2;
                    Y2 = h1;
                    Y3 = l1;
                    Y4 = h1;
                    Y5 = h2;
                    gapvalues();
                    createbitmap();
                    drawpath();
                } else if (spinnerposition > 11) {
                    List<Presetvalues> presetvalues = db.getpresetbyid(spinnerposition - 11);
                    for (Presetvalues pv : presetvalues) {
                        Y1 = pv.getPY1();
                        Y2 = pv.getPY2();
                        Y3 = pv.getPY3();
                        Y4 = pv.getPY4();
                        Y5 = pv.getPY5();
                        pathcolor();
                        gapvalues();
                        createbitmap();
                        drawpath();
                        setupEqualizerFxAndUI();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                setupEqualizerFxAndUI();
            }
        });
    }

    private void ShowNewPresetDialog() {
        dialog = new Dialog(this, R.style.AmuzeTheme);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 210, getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.dialogbg);
        LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.getWindow().setAttributes(lp);
        window.addFlags(LayoutParams.FLAG_DIM_BEHIND);

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.dialog_new_preset, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);

        dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        EditText editText = (EditText) dialog.findViewById(R.id.ti);
        editText.requestFocus();

        Button okButton = (Button) dialog.findViewById(R.id.dBOK);
        okButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText = (EditText) dialog.findViewById(R.id.ti);
                Utilities.HideSoftKeyboard(EqualizerActivity.this, editText);
                String name = editText.getText().toString();
                if (!Utilities.isEmpty(name)) {

                    db.addPreset(new Presetvalues(name, Y1, Y2, Y3, Y4, Y5));
                    spinnerList.add(name);
                    curPresetIndex = spinnerList.size() - 1;
                    spinner.setSelection(curPresetIndex);
                    dialog.dismiss();
                }
            }
        });

        Button cButton = (Button) dialog.findViewById(R.id.dBCancel);
        cButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText = (EditText) dialog.findViewById(R.id.ti);
                Utilities.HideSoftKeyboard(EqualizerActivity.this, editText);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void adapterlist() {
        presetcount = db.getpresetCount();
        Log.d("music player preset", "count is  " + presetcount);
        spinnerList = new ArrayList<>();
        spinnerList.add(getResources().getString(R.string.preset_current));
        spinnerList.add(getResources().getString(R.string.new_preset));
        spinnerList.add(getResources().getString(R.string.preset_normal));
        spinnerList.add(getResources().getString(R.string.preset_classical));
        spinnerList.add(getResources().getString(R.string.preset_dance));
        spinnerList.add(getResources().getString(R.string.preset_flat));
        spinnerList.add(getResources().getString(R.string.preset_folk));
        spinnerList.add(getResources().getString(R.string.preset_heavy_metal));
        spinnerList.add(getResources().getString(R.string.preset_hip_hop));
        spinnerList.add(getResources().getString(R.string.preset_jazz));
        spinnerList.add(getResources().getString(R.string.preset_pop));
        spinnerList.add(getResources().getString(R.string.preset_rock));
        if (presetcount != 0) {
            for (int i = 0; i <= presetcount; i++) {
                List<Presetvalues> presetvalues = db.getpresetbyid(i);
                for (Presetvalues pv : presetvalues) {
                    String pname = pv.getPRESETNAME();
                    spinnerList.add(pname);
                }
            }
        }
    }

    private void createbitmap() {
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(size);
        int width = size.x;
        int height = size.y;
        bitmap = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        drawingImageView.setImageBitmap(bitmap);
    }

    //sets band level of single one based on location of Y
    private void setupEqualizerFxAndUI() {

        h1 = DeviceHeight * 40 / 100;
        h2 = DeviceHeight * 35 / 100;
        h3 = DeviceHeight * 30 / 100;
        h4 = DeviceHeight * 25 / 100;
        h5 = DeviceHeight * 20 / 100;
        mid = DeviceHeight * 45 / 100;
        l1 = DeviceHeight * 50 / 100;
        l2 = DeviceHeight * 55 / 100;
        l3 = DeviceHeight * 60 / 100;
        l4 = DeviceHeight * 65 / 100;
        l5 = DeviceHeight * 70 / 100;

        if (Y > mid && Y <= h1) {
            Singleton.theEqualizer.setBandLevel(band, (short) 300);
            return;
        } else if (Y < h1 && Y >= h2) {
            Singleton.theEqualizer.setBandLevel(band, (short) 600);
            return;
        } else if (Y < h2 && Y >= h3) {
            Singleton.theEqualizer.setBandLevel(band, (short) 900);
            return;
        } else if (Y < h3 && Y >= h4) {
            Singleton.theEqualizer.setBandLevel(band, (short) 1200);
            return;
        } else if (Y < h4 && Y >= h5) {
            Singleton.theEqualizer.setBandLevel(band, (short) 1500);
            return;
        } else if (Y == mid) {
            Singleton.theEqualizer.setBandLevel(band, (short) 0);
            return;
        } else if (Y > mid && Y <= l1) {
            Singleton.theEqualizer.setBandLevel(band, (short) -300);
            return;
        } else if (Y > l1 && Y <= l2) {
            Singleton.theEqualizer.setBandLevel(band, (short) -600);
            return;
        } else if (Y > l2 && Y <= l3) {
            Singleton.theEqualizer.setBandLevel(band, (short) -900);
            return;
        } else if (Y > l3 && Y <= l4) {
            Singleton.theEqualizer.setBandLevel(band, (short) -1200);
            return;
        } else if (Y > l4 && Y <= l5) {
            Singleton.theEqualizer.setBandLevel(band, (short) -1500);
            return;
        }

        short bandlevel1 = Singleton.theEqualizer.getBandLevel(band);
        Log.d("name ++value+++++" + Y, "current preset name is" + band);
        Log.d("name ++value+++++" + Y, "current preset name is" + bandlevel1);
    }

    private void drawpath() {
        // path 1
        Path p1 = new Path();
        p1.moveTo(W1, Mid);
        p1.cubicTo(X1 - 10, Y1, X1 + 10, Y1, W2, I1);
        canvas.drawPath(p1, paint1);
        // path 2
        Path p2 = new Path();
        p2.moveTo(W3, I2);
        p2.cubicTo(X2 - 10, Y2, X2 + 10, Y2, W4, I2);
        canvas.drawPath(p2, paint2);
        // path 3
        Path p3 = new Path();
        p3.moveTo(W5, I3);
        p3.cubicTo(X3 - 10, Y3, X3 + 10, Y3, W6, I3);
        canvas.drawPath(p3, paint3);
        // path 4
        Path p4 = new Path();
        p4.moveTo(W7, I4);
        p4.cubicTo(X4 - 10, Y4, X4 + 10, Y4, W8, I4);
        canvas.drawPath(p4, paint4);
        // path 5
        Path p5 = new Path();
        p5.moveTo(W9, I5);
        p5.cubicTo(X5 - 10, Y5, X5 + 10, Y5, W10, Mid);
        canvas.drawPath(p5, paint5);

        // path m1
        Path m1 = new Path();
        m1.moveTo(W2, I1);
        m1.cubicTo(Mgap1, Mid, Mgap1, Mid, W3, I2);
        canvas.drawPath(m1, midcolor);
        // path m2
        Path m2 = new Path();
        m2.moveTo(W4, I2);
        m2.cubicTo(Mgap2, Mid, Mgap2, Mid, W5, I3);
        canvas.drawPath(m2, midcolor);
        // path m3
        Path m3 = new Path();
        m3.moveTo(W6, I3);
        m3.cubicTo(Mgap3, Mid, Mgap3, Mid, W7, I4);
        canvas.drawPath(m3, midcolor);
        // path m4
        Path m4 = new Path();
        m4.moveTo(W8, I4);
        m4.cubicTo(Mgap4, Mid, Mgap4, Mid, W9, I5);
        canvas.drawPath(m4, midcolor);

        // glow1
        Path g1 = new Path();
        g1.moveTo(X1, C1);
        g1.addCircle(X1, C1, 10, Path.Direction.CW);
        canvas.drawPath(g1, glowcolor1);
        // glow2
        Path g2 = new Path();
        g2.moveTo(X2, C2);
        g2.addCircle(X2, C2, 10, Path.Direction.CW);
        canvas.drawPath(g2, glowcolor2);
        // glow3
        Path g3 = new Path();
        g3.moveTo(X3, C3);
        g3.addCircle(X3, C3, 10, Path.Direction.CW);
        canvas.drawPath(g3, glowcolor3);
        // glow4
        Path g4 = new Path();
        g4.moveTo(X4, C4);
        g4.addCircle(X4, C4, 10, Path.Direction.CW);
        canvas.drawPath(g4, glowcolor4);
        // glow5
        Path g5 = new Path();
        g5.moveTo(X5, C5);
        g5.addCircle(X5, C5, 10, Path.Direction.CW);
        canvas.drawPath(g5, glowcolor5);
        // circle1
        Path c1 = new Path();
        c1.moveTo(X1, C1);
        c1.addCircle(X1, C1, 12, Path.Direction.CW);
        canvas.drawPath(c1, circle1);
        // circle2
        Path c2 = new Path();
        c2.moveTo(X2, C2);
        c2.addCircle(X2, C2, 12, Path.Direction.CW);
        canvas.drawPath(c2, circle2);
        // circle3
        Path c3 = new Path();
        c3.moveTo(X3, C3);
        c3.addCircle(X3, C3, 12, Path.Direction.CW);
        canvas.drawPath(c3, circle3);
        // circle4
        Path c4 = new Path();
        c4.moveTo(X4, C4);
        c4.addCircle(X4, C4, 12, Path.Direction.CW);
        canvas.drawPath(c4, circle4);
        // circle5
        Path c5 = new Path();
        c5.moveTo(X5, C5);
        c5.addCircle(X5, C5, 12, Path.Direction.CW);
        canvas.drawPath(c5, circle5);

    }

    private void volumecalc() {
        if (per2 == 0) {
            volume = 0;
        } else if (per2 <= 6 && per2 > 0) {
            volume = 1;
        } else if (per2 <= 13 && per2 > 6) {
            volume = 2;
        } else if (per2 <= 20 && per2 > 13) {
            volume = 3;
        } else if (per2 <= 26 && per2 > 20) {
            volume = 4;
        } else if (per2 <= 33 && per2 > 26) {
            volume = 5;
        } else if (per2 <= 40 && per2 > 33) {
            volume = 6;
        } else if (per2 <= 46 && per2 > 40) {
            volume = 7;
        } else if (per2 <= 53 && per2 > 40) {
            volume = 8;
        } else if (per2 <= 59 && per2 > 53) {
            volume = 9;
        } else if (per2 <= 66 && per2 > 59) {
            volume = 10;
        } else if (per2 <= 73 && per2 > 66) {
            volume = 11;
        } else if (per2 <= 79 && per2 > 73) {
            volume = 12;
        } else if (per2 <= 86 && per2 > 79) {
            volume = 13;
        } else if (per2 <= 94 && per2 > 86) {
            volume = 14;
        } else if (per2 <= 99 && per2 > 94) {
            volume = 15;
        }
    }

    private void statorselectorBassBoost() {
        if (per1 >= 0 && per1 < 5) {
            stator1 = R.drawable.stator1;
        } else if (per1 >= 5 && per1 < 10) {
            stator1 = R.drawable.stator2;
        } else if (per1 >= 10 && per1 < 15) {
            stator1 = R.drawable.stator3;
        } else if (per1 >= 15 && per1 < 20) {
            stator1 = R.drawable.stator4;
        } else if (per1 >= 20 && per1 < 24) {
            stator1 = R.drawable.stator5;
        } else if (per1 >= 24 && per1 < 29) {
            stator1 = R.drawable.stator6;
        } else if (per1 >= 29 && per1 < 34) {
            stator1 = R.drawable.stator7;
        } else if (per1 >= 34 && per1 < 39) {
            stator1 = R.drawable.stator8;
        } else if (per1 >= 39 && per1 < 44) {
            stator1 = R.drawable.stator9;
        } else if (per1 >= 44 && per1 < 50) {
            stator1 = R.drawable.stator10;
        } else if (per1 >= 50 && per1 < 54) {
            stator1 = R.drawable.stator11;
        } else if (per1 >= 54 && per1 < 58) {
            stator1 = R.drawable.stator12;
        } else if (per1 >= 58 && per1 < 64) {
            stator1 = R.drawable.stator13;
        } else if (per1 >= 64 && per1 < 69) {
            stator1 = R.drawable.stator14;
        } else if (per1 >= 69 && per1 < 74) {
            stator1 = R.drawable.stator15;
        } else if (per1 >= 74 && per1 < 80) {
            stator1 = R.drawable.stator16;
        } else if (per1 >= 80 && per1 < 84) {
            stator1 = R.drawable.stator17;
        } else if (per1 >= 84 && per1 < 90) {
            stator1 = R.drawable.stator18;
        } else if (per1 >= 90 && per1 < 95) {
            stator1 = R.drawable.stator19;
        } else if (per1 >= 95 && per1 < 100) {
            stator1 = R.drawable.stator20;
        }
    }

    private void statorselectorVolume() {
        if (per2 >= 0 && per2 < 5) {
            stator2 = R.drawable.stator1;
        } else if (per2 >= 5 && per2 < 10) {
            stator2 = R.drawable.stator2;
        } else if (per2 >= 10 && per2 < 15) {
            stator2 = R.drawable.stator3;
        } else if (per2 >= 15 && per2 < 20) {
            stator2 = R.drawable.stator4;
        } else if (per2 >= 20 && per2 < 24) {
            stator2 = R.drawable.stator5;
        } else if (per2 >= 24 && per2 < 29) {
            stator2 = R.drawable.stator6;
        } else if (per2 >= 29 && per2 < 34) {
            stator2 = R.drawable.stator7;
        } else if (per2 >= 34 && per2 < 39) {
            stator2 = R.drawable.stator8;
        } else if (per2 >= 39 && per2 < 44) {
            stator2 = R.drawable.stator9;
        } else if (per2 >= 44 && per2 < 50) {
            stator2 = R.drawable.stator10;
        } else if (per2 >= 50 && per2 < 54) {
            stator2 = R.drawable.stator11;
        } else if (per2 >= 54 && per2 < 58) {
            stator2 = R.drawable.stator12;
        } else if (per2 >= 58 && per2 < 64) {
            stator2 = R.drawable.stator13;
        } else if (per2 >= 64 && per2 < 69) {
            stator2 = R.drawable.stator14;
        } else if (per2 >= 69 && per2 < 74) {
            stator2 = R.drawable.stator15;
        } else if (per2 >= 74 && per2 < 80) {
            stator2 = R.drawable.stator16;
        } else if (per2 >= 80 && per2 < 84) {
            stator2 = R.drawable.stator17;
        } else if (per2 >= 84 && per2 < 90) {
            stator2 = R.drawable.stator18;
        } else if (per2 >= 90 && per2 < 95) {
            stator2 = R.drawable.stator19;
        } else if (per2 >= 95 && per2 < 100) {
            stator2 = R.drawable.stator20;
        }
    }

    private void statorselectorVirtualizer() {
        if (per3 >= 0 && per3 < 5) {
            stator3 = R.drawable.stator1;
        } else if (per3 >= 5 && per3 < 10) {
            stator3 = R.drawable.stator2;
        } else if (per3 >= 10 && per3 < 15) {
            stator3 = R.drawable.stator3;
        } else if (per3 >= 15 && per3 < 20) {
            stator3 = R.drawable.stator4;
        } else if (per3 >= 20 && per3 < 24) {
            stator3 = R.drawable.stator5;
        } else if (per3 >= 24 && per3 < 29) {
            stator3 = R.drawable.stator6;
        } else if (per3 >= 29 && per3 < 34) {
            stator3 = R.drawable.stator7;
        } else if (per3 >= 34 && per3 < 39) {
            stator3 = R.drawable.stator8;
        } else if (per3 >= 39 && per3 < 44) {
            stator3 = R.drawable.stator9;
        } else if (per3 >= 44 && per3 < 50) {
            stator3 = R.drawable.stator10;
        } else if (per3 >= 50 && per3 < 54) {
            stator3 = R.drawable.stator11;
        } else if (per3 >= 54 && per3 < 58) {
            stator3 = R.drawable.stator12;
        } else if (per3 >= 58 && per3 < 64) {
            stator3 = R.drawable.stator13;
        } else if (per3 >= 64 && per3 < 69) {
            stator3 = R.drawable.stator14;
        } else if (per3 >= 69 && per3 < 74) {
            stator3 = R.drawable.stator15;
        } else if (per3 >= 74 && per3 < 80) {
            stator3 = R.drawable.stator16;
        } else if (per3 >= 80 && per3 < 84) {
            stator3 = R.drawable.stator17;
        } else if (per3 >= 84 && per3 < 90) {
            stator3 = R.drawable.stator18;
        } else if (per3 >= 90 && per3 < 95) {
            stator3 = R.drawable.stator19;
        } else if (per3 >= 95 && per3 < 100) {
            stator3 = R.drawable.stator20;
        }

    }

    //on touching screen ==> finding value of presets
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Xvalue = (int) event.getX();
        Yvalue = (int) event.getY();
        Y = Yvalue;

        if ((Yvalue <= Boder_bottom) && (Yvalue > Boder_top)) {
            //SetupSpinner();
            spinner.setSelection(0);
            int Action = event.getActionMasked();
            switch (Action) {
                case MotionEvent.ACTION_DOWN:
                    if (Xvalue > W1 && Xvalue < W2) {
                        X1 = W1 + ((W2 - W1) / 2);
                        Y1 = Yvalue;
                        setfocus = 1;
                        band = 0;

                    } else if (Xvalue > W3 && Xvalue < W4) {
                        X2 = W3 + (W4 - W3) / 2;
                        Y2 = Yvalue;

                        band = (short) 1;
                        setfocus = 2;
                    } else if (Xvalue > W5 && Xvalue < W6) {
                        X3 = W5 + (W6 - W5) / 2;
                        Y3 = Yvalue;

                        band = (short) 2;
                        setfocus = 3;
                    } else if (Xvalue > W7 && Xvalue < W8) {
                        X4 = W7 + (W8 - W7) / 2;
                        Y4 = Yvalue;

                        band = (short) 3;
                        setfocus = 4;
                    } else if (Xvalue > W9 && Xvalue < W10) {
                        X5 = W9 + (W10 - W9) / 2;
                        Y5 = Yvalue;

                        band = (short) 4;
                        setfocus = 5;
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (setfocus == 1) {
                        Y1 = Yvalue;
                    } else if (setfocus == 2) {
                        Y2 = Yvalue;
                    } else if (setfocus == 3) {
                        Y3 = Yvalue;
                    } else if (setfocus == 4) {
                        Y4 = Yvalue;
                    } else if (setfocus == 5) {
                        Y5 = Yvalue;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    setfocus = 0;
                    break;
            }
            db.updateContact(new Pathvalues(1, Y1, Y2, Y3, Y4, Y5));
            List<Pathvalues> pathvalues = db.getAllContacts();
            for (Pathvalues cn : pathvalues) {
                String logontouch = "id" + cn.getID() + cn.getY1() + cn.getY2() + cn.getY3() + cn.getY4() + cn.getY5();
                Log.d("Value: ", logontouch);
            }
            setupEqualizerFxAndUI();
            pathcolor();
            gapvalues();
            createbitmap();
            drawpath();
        }
        return false;
    }

    //mostly for drawing stuff

    private void gapvalues() {
        // TODO Auto-generated method stub
        if (Y1 > Mid) {
            D1 = ((Y1 - Mid) * 30) / 100;
            I1 = Mid + D1;
            C1 = Y1 - (((Y1 - Mid) * 20) / 100);
            paint1.setShader(shaderBottom);
            circle1.setColor(getResources().getColor(R.color.graphbottomcolor));
            glowcolor1.setColor(getResources().getColor(R.color.glowcolorbottom));
        } else if (Y1 < Mid) {
            D1 = ((Mid - Y1) * 30) / 100;
            I1 = Mid - D1;
            C1 = Y1 + (((Mid - Y1) * 20) / 100);
            paint1.setShader(shaderTop);
            circle1.setColor(getResources().getColor(R.color.graphtopcolor));
            glowcolor1.setColor(getResources().getColor(R.color.glowcolortop));
        } else if (Y1 == Mid) {
            I1 = Mid;
            C1 = Y1;
            paint1.setShader(shaderNormal);
            circle1.setColor(getResources().getColor(R.color.graphnormalcolor));
            glowcolor1.setColor(getResources().getColor(R.color.glowcolornormal));
        }

        if (Y2 > Mid) {
            D2 = ((Y2 - Mid) * 30) / 100;
            I2 = Mid + D2;
            C2 = Y2 - (((Y2 - Mid) * 20) / 100);
            paint2.setShader(shaderBottom);
            circle2.setColor(getResources().getColor(R.color.graphbottomcolor));
            glowcolor2.setColor(getResources().getColor(R.color.glowcolorbottom));
        } else if (Y2 < Mid) {
            D2 = ((Mid - Y2) * 30) / 100;
            I2 = Mid - D2;
            C2 = Y2 + (((Mid - Y2) * 20) / 100);
            paint2.setShader(shaderTop);
            circle2.setColor(getResources().getColor(R.color.graphtopcolor));
            glowcolor2.setColor(getResources().getColor(R.color.glowcolortop));
        } else if (Y2 == Mid) {
            I2 = Mid;
            C2 = Y2;
            Singleton.theEqualizer.setEnabled(true);
            paint2.setShader(shaderNormal);
            circle2.setColor(getResources().getColor(R.color.graphnormalcolor));
            glowcolor2.setColor(getResources().getColor(R.color.glowcolornormal));
        }

        if (Y3 > Mid) {
            D3 = ((Y3 - Mid) * 30) / 100;
            I3 = Mid + D3;
            C3 = Y3 - (((Y3 - Mid) * 20) / 100);
            paint3.setShader(shaderBottom);
            circle3.setColor(getResources().getColor(R.color.graphbottomcolor));
            glowcolor3.setColor(getResources().getColor(R.color.glowcolorbottom));
        } else if (Y3 < Mid) {
            D3 = ((Mid - Y3) * 30) / 100;
            I3 = Mid - D3;
            C3 = Y3 + (((Mid - Y3) * 20) / 100);
            paint3.setShader(shaderTop);
            circle3.setColor(getResources().getColor(R.color.graphtopcolor));
            glowcolor3.setColor(getResources().getColor(R.color.glowcolortop));
        } else if (Y3 == Mid) {
            I3 = Mid;
            C3 = Y3;
            paint3.setShader(shaderNormal);
            circle3.setColor(getResources().getColor(R.color.graphnormalcolor));
            glowcolor3.setColor(getResources().getColor(R.color.glowcolornormal));
        }

        if (Y4 > Mid) {
            D4 = ((Y4 - Mid) * 30) / 100;
            I4 = Mid + D4;
            C4 = Y4 - (((Y4 - Mid) * 20) / 100);
            paint4.setShader(shaderBottom);
            circle4.setColor(getResources().getColor(R.color.graphbottomcolor));
            glowcolor4.setColor(getResources().getColor(R.color.glowcolorbottom));
        } else if (Y4 < Mid) {
            D4 = ((Mid - Y4) * 30) / 100;
            I4 = Mid - D4;
            C4 = Y4 + (((Mid - Y4) * 20) / 100);
            paint4.setShader(shaderTop);
            circle4.setColor(getResources().getColor(R.color.graphtopcolor));
            glowcolor4.setColor(getResources().getColor(R.color.glowcolortop));
        } else if (Y4 == Mid) {
            I4 = Mid;
            C4 = Y4;
            paint4.setShader(shaderNormal);
            circle4.setColor(getResources().getColor(R.color.graphnormalcolor));
            glowcolor4.setColor(getResources().getColor(R.color.glowcolornormal));
        }

        if (Y5 > Mid) {
            D5 = ((Y5 - Mid) * 30) / 100;
            I5 = Mid + D5;
            C5 = Y5 - (((Y5 - Mid) * 20) / 100);
            paint5.setShader(shaderBottom);
            circle5.setColor(getResources().getColor(R.color.graphbottomcolor));
            glowcolor5.setColor(getResources().getColor(R.color.glowcolorbottom));
        } else if (Y5 < Mid) {
            D5 = ((Mid - Y5) * 30) / 100;
            I5 = Mid - D5;
            C5 = Y5 + (((Mid - Y5) * 20) / 100);
            paint5.setShader(shaderTop);
            circle5.setColor(getResources().getColor(R.color.graphtopcolor));
            glowcolor5.setColor(getResources().getColor(R.color.glowcolortop));

        } else if (Y5 == Mid) {
            I5 = Mid;
            C5 = Y5;
            paint5.setShader(shaderNormal);
            circle5.setColor(getResources().getColor(R.color.graphnormalcolor));
            glowcolor5.setColor(getResources().getColor(R.color.glowcolornormal));
        }
    }

    @Override
    public String GetActivityID() {
        return "EqualizerActivity";
    }

    @Override
    public int GetLayoutResID() {
        // TODO Auto-generated method stub
        return R.layout.equalizer_vertical;
    }

    @Override
    public void OnBackPressed() {
        finish();
    }

    @Override
    protected String GetGAScreenName() {
        return "EqualizerActivity";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            percentcalcVolume();
            statorselectorVolume();
            rv2.setRotorPercentage(per2);
            ivBack2.setImageResource(stator2);
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            percentcalcVolume();
            statorselectorVolume();
            rv2.setRotorPercentage(per2);
            ivBack2.setImageResource(stator2);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setBassBoost(BassBoost bassBoost, int percent) {
        try {
            bassBoost.setStrength((short) ((short) 1000 / 100 * percent));
            bassBoost.setEnabled(true);
        } catch (Exception ignored) {
        }
    }

    private void setBassBoostOff(BassBoost bassBoost) {
        bassBoost.setEnabled(false);
    }

    private void setVirtualizer(Virtualizer virtualizer, int percent) {
        try {
            virtualizer.setStrength((short) ((short) 1000 / 100 * percent));
            virtualizer.setEnabled(true);
        } catch (Exception ignored) {
        }
    }

    private void setVirtualizerOff(Virtualizer virtualizer) {
        virtualizer.setEnabled(false);
    }

    private void initializeEqualizers() {
        if (Singleton.theEqualizer == null) {
            try {
                Singleton.theEqualizer = new Equalizer(0, Music_service.mp.getAudioSessionId());

            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        if (bassBoost == null) {
            try {
                bassBoost = new BassBoost(0, Music_service.mp.getAudioSessionId());

            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        if (virtualizer == null) {
            try {
                virtualizer = new Virtualizer(0, Music_service.mp.getAudioSessionId());
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        bassBoost = Singleton.theBooster;
        virtualizer = Singleton.theVirtualizer;
        mEqualizer = Singleton.theEqualizer;

    }
}
