package com.example.home.TraX;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.VideoView;


/**
 * Created by Home on 2017-05-01.
 */

public class SplashScreen  extends Activity {

        /** Duration of wait **/
        private final int SPLASH_DISPLAY_LENGTH = 50000;
    VideoView videoview;
    Button button;
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.splashscreen);


            videoview = (VideoView) findViewById(R.id.videoview);
            button = (Button) findViewById(R.id.skip);

            Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.spalsh);

            videoview.setVideoURI(uri);
            videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            videoview.start();
            button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            });

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
            /*new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);*/




        }
}
