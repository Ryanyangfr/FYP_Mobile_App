package com.smu.engagingu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.smu.engagingu.fyp.R;
import cn.easyar.Engine;

public class ARPage extends AppCompatActivity {

    private static String key = "OUTHnu9jKndd02morsKcKhltLClYoKsL0ftFasrY7OLOg93PZEDJOm3QVpz6ICAdrIJivmjHiEuvW8qtLk4c7tjuTTvp8LAx78EopYHdE70r4TAAorr3IqD5SGIDUQRj47tGDfWduvB67bbcQVUuji9BJJoUIlV9zEILITgVmnTjsFBZ6i27mlPs9b4JeIRn71gcp67V";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arpage);

        if(!Engine.initialize(this, key)){
            Log.e("HelloAR", "Initialization Failed.");
        };
    }
}
