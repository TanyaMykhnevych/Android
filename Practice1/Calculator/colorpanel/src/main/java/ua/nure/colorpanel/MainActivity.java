package ua.nure.colorpanel;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private int seekR, seekG, seekB;
    private LinearLayout colorPanel;

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.RedSeekBar:
                    seekR = progress;
                    break;
                case R.id.GreenSeekBar:
                    seekG = progress;
                    break;
                case R.id.BlueSeekBar:
                    seekB = progress;
                    break;
            }

            changeColor();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar sbR = (SeekBar) findViewById(R.id.RedSeekBar);
        SeekBar sbG = (SeekBar) findViewById(R.id.GreenSeekBar);
        SeekBar sbB = (SeekBar) findViewById(R.id.BlueSeekBar);
        colorPanel = (LinearLayout) findViewById(R.id.ColorPanel);


        sbR.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sbG.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sbB.setOnSeekBarChangeListener(onSeekBarChangeListener);

    }

    private void changeColor() {
        int color = Color.rgb(seekR, seekG, seekB);
        colorPanel.setBackgroundColor(color);
    }
}
