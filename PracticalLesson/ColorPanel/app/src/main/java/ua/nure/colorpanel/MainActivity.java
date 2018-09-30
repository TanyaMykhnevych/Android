package ua.nure.colorpanel;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private SeekBar sbR, sbG, sbB;
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
            changeColor();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sbR = (SeekBar) findViewById(R.id.RedSeekBar);
        sbG = (SeekBar) findViewById(R.id.GreenSeekBar);
        sbB = (SeekBar) findViewById(R.id.BlueSeekBar);
        colorPanel = (LinearLayout) findViewById(R.id.ColorPanel);

        changeColor();

        sbR.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sbG.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sbB.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    private void changeColor() {
        int seekR, seekG, seekB;
        seekR = sbR.getProgress();
        seekG = sbG.getProgress();
        seekB = sbB.getProgress();
        colorPanel.setBackgroundColor(Color.rgb(seekR, seekG, seekB));
    }
}
