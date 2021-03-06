package ua.nure.musicplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import ua.nure.musicplayer.adapters.PlayListAdapter;
import ua.nure.musicplayer.models.Audio;
import ua.nure.musicplayer.models.PlaybackStatus;
import ua.nure.musicplayer.services.AudioPlayerService;
import ua.nure.musicplayer.utils.ProgressUtils;
import ua.nure.musicplayer.utils.StorageUtil;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final int MY_PERMISSION_REQUEST = 1;

    private AudioPlayerService _player;
    private boolean _serviceBound = false;
    private ArrayList<Audio> _audioList;
    private Audio _currentTrack;
    private int _currentTrackPosition = 0;
    private boolean _isPaused = true;
    private boolean _shuffleAudio = false;
    private boolean _repeatAudio = false;
    private int _repeatAudioCount = 0;
    private Handler _mHandler = new Handler();

    private ListView _listView;
    private PlayListAdapter _adapter;
    private TextView _currentAudioTitle;
    private TextView _audioTime;
    private SeekBar _progressBar;
    private ImageButton _shuffleBtn;
    private ImageButton _prevBtn;
    private ImageButton _nextBtn;
    private ImageButton _playPauseBtn;
    private ImageButton _repeatBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            loadAudio();
        }

        initViewElements();
        registerGetNextAudio();
    }

    @Override
    protected void onPause() {
        _player.buildNotification(_isPaused ? PlaybackStatus.PAUSED : PlaybackStatus.PLAYING);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        _player.removeNotification();
        super.onRestart();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", _serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        _serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        loadAudio();
                    } else {
                        finish();
                    }
                }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        _mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        _mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = _player.getCurrentAudioDuration();
        int currentPosition = ProgressUtils.progressToTimer(seekBar.getProgress(), totalDuration);

        _player.seekTo(currentPosition);
        updateProgressBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_serviceBound) {
            unbindService(serviceConnection);
            _player.stopSelf();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) service;
            _player = binder.getService();
            _serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            _serviceBound = false;
        }
    };

    private void initViewElements() {
        _currentAudioTitle = ((TextView) this.findViewById(R.id.audioTitle));
        _progressBar = ((SeekBar) this.findViewById(R.id.audioProgressBar));
        _shuffleBtn = ((ImageButton) this.findViewById(R.id.shuffleBtn));
        _prevBtn = ((ImageButton) this.findViewById(R.id.prevBtn));
        _nextBtn = ((ImageButton) this.findViewById(R.id.nextBtn));
        _repeatBtn = ((ImageButton) this.findViewById(R.id.repeatBtn));
        _playPauseBtn = ((ImageButton) this.findViewById(R.id.playPauseBtn));
        _audioTime = ((TextView) this.findViewById(R.id.audioTime));

        _adapter = new PlayListAdapter(this, R.layout.playlist_item, _audioList);
        _listView = findViewById(R.id.list);
        _listView.setAdapter(_adapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                _currentTrack = _audioList.get(position);
                _currentTrackPosition = position;
                playAudio(position);
            }
        });

        _currentAudioTitle.setText("Please, select audio to play");
        _progressBar.setOnSeekBarChangeListener(this);
        setButtonListeners();
    }

    private void playAudio(int audioIndex) {
        _isPaused = false;
        if (!_serviceBound) {
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(_audioList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, AudioPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            Intent broadcastIntent = new Intent(AudioPlayerService.BROADCAST_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
        processIsPaused();
        setupProgressBar();
        updateCurrentAudioView();
    }

    private BroadcastReceiver getNextAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playNextAudio();
        }
    };

    private void loadAudio() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            _audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                _audioList.add(new Audio(data, title, album, artist));
            }
            cursor.close();
        }
    }

    private void updateCurrentAudioView() {
        _currentAudioTitle.setText(_currentTrack.getTitle() + " " + _currentTrack.getArtist());
        updateProgressBar();
    }

    private void setButtonListeners() {
        setNextButtonListener();
        setPrevButtonListener();
        playPauseButtonListener();
        shuffleButtonListener();
        repeatButtonListener();
    }

    private void setNextButtonListener() {
        _nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextAudio();
            }
        });
    }

    private void setPrevButtonListener() {
        _prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _currentTrackPosition = _currentTrackPosition == 0 ?
                        _audioList.size() - 1 :
                        _currentTrackPosition - 1;
                _currentTrack = _audioList.get(_currentTrackPosition);
                updateCurrentAudioView();
                playAudio(_currentTrackPosition);
            }
        });
    }

    private void playPauseButtonListener() {
        _playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isPaused = !_isPaused;
                processIsPaused();
            }
        });
    }

    private void shuffleButtonListener() {
        _shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _shuffleAudio = !_shuffleAudio;
                if (_shuffleAudio) {
                    _shuffleBtn.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorChosen));
                } else {
                    _shuffleBtn.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorAccent));
                }
            }
        });
    }

    private void repeatButtonListener() {
        _repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _repeatAudio = !_repeatAudio;
                _repeatAudioCount = 0;
                if (_repeatAudio) {
                    _repeatBtn.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorChosen));
                } else {
                    _repeatBtn.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorAccent));
                }
            }
        });
    }

    private void processIsPaused() {
        if (_isPaused) {
            _playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
            Intent broadcastIntent = new Intent(AudioPlayerService.BROADCAST_PAUSE_AUDIO);
            sendBroadcast(broadcastIntent);
        } else {
            _playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
            Intent broadcastIntent = new Intent(AudioPlayerService.BROADCAST_RESUME_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    private void setupProgressBar() {
        _progressBar.setProgress(0);
        _progressBar.setMax(100);
    }

    private void updateProgressBar() {
        _mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private void playNextAudio() {
        _progressBar.setProgress(0);
        if (_repeatAudio && _repeatAudioCount < 1) {
            _repeatAudioCount++;
        } else {
            _repeatAudioCount = 0;
            if (!_shuffleAudio) {
                _currentTrackPosition = _currentTrackPosition == _audioList.size() - 1 ?
                        0 :
                        _currentTrackPosition + 1;
            } else {
                Random random = new Random();
                int nextPos = random.nextInt(_audioList.size());
                while (nextPos == _currentTrackPosition) {
                    nextPos = random.nextInt(_audioList.size());
                }

                _currentTrackPosition = nextPos;
            }
        }

        _currentTrack = _audioList.get(_currentTrackPosition);
        updateCurrentAudioView();
        playAudio(_currentTrackPosition);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = _player == null ? 0 : _player.getCurrentAudioDuration();
            long currentDuration = _player == null ? 0 : _player.getCurrentAudioPosition();

            if (currentDuration > totalDuration - 100) {
                currentDuration = totalDuration;
            }

            _audioTime.setText(ProgressUtils.milliSecondsToTimer(currentDuration) +
                    " / " +
                    ProgressUtils.milliSecondsToTimer(totalDuration));

            int progress = (int) (ProgressUtils.getProgressPercentage(currentDuration, totalDuration));
            _progressBar.setProgress(progress);

            _mHandler.postDelayed(this, 100);
        }
    };

    private void registerGetNextAudio() {
        IntentFilter filter = new IntentFilter(AudioPlayerService.BROADCAST_GET_NEXT_AUDIO);
        registerReceiver(getNextAudio, filter);
    }
}
