package ua.nure.musicplayer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import ua.nure.musicplayer.MainActivity;
import ua.nure.musicplayer.R;
import ua.nure.musicplayer.models.Audio;
import ua.nure.musicplayer.models.PlaybackStatus;
import ua.nure.musicplayer.utils.StorageUtil;

public class AudioPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    private final IBinder _iBinder = new LocalBinder();
    private MediaPlayer _mediaPlayer;
    private AudioManager _audioManager;
    private int _resumePosition;
    private boolean _needToAutoResume = true;
    private ArrayList<Audio> _audioList;
    private int _audioIndex = -1;
    private Audio _activeAudio;
    private MediaSessionManager _mediaSessionManager;
    private MediaSessionCompat _mediaSession;
    private MediaControllerCompat.TransportControls _transportControls;
    private static final int NOTIFICATION_ID = 101;
    private boolean _ongoingCall = false;
    private PhoneStateListener _phoneStateListener;
    private TelephonyManager _telephonyManager;


    @Override
    public IBinder onBind(Intent intent) {
        return _iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        callStateListener();
        registerPlayNewAudio();
        registerPauseAudio();
        registerResumeAudio();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (_mediaPlayer == null) initMediaPlayer();
                else if (!_mediaPlayer.isPlaying() && _needToAutoResume) _mediaPlayer.start();
                _mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (_mediaPlayer.isPlaying()) _mediaPlayer.stop();
                _mediaPlayer.release();
                _mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (_mediaPlayer.isPlaying()) _mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (_mediaPlayer.isPlaying()) _mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            StorageUtil storage = new StorageUtil(getApplicationContext());
            _audioList = storage.loadAudio();
            _audioIndex = storage.loadAudioIndex();

            if (_audioIndex != -1 && _audioIndex < _audioList.size()) {
                _activeAudio = _audioList.get(_audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        if (!requestAudioFocus()) {
            stopSelf();
        }

        if (_mediaSessionManager == null) {
            initMediaSession();
            initMediaPlayer();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        _mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_mediaPlayer != null) {
            stopMedia();
            _mediaPlayer.release();
        }
        removeAudioFocus();
    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    public int getCurrentAudioPosition() {
        return _mediaPlayer.getCurrentPosition();
    }

    public int getCurrentAudioDuration() {
        return _mediaPlayer.getDuration();
    }

    public void seekTo(int position) {
        _mediaPlayer.seekTo(position);
    }

    private void initMediaPlayer() {
        if (_mediaPlayer == null)
            _mediaPlayer = new MediaPlayer();

        _mediaPlayer.setOnCompletionListener(this);
        _mediaPlayer.setOnErrorListener(this);
        _mediaPlayer.setOnPreparedListener(this);
        _mediaPlayer.setOnBufferingUpdateListener(this);
        _mediaPlayer.setOnSeekCompleteListener(this);
        _mediaPlayer.setOnInfoListener(this);
        _mediaPlayer.reset();

        _mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            _mediaPlayer.setDataSource(_activeAudio.getData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        _mediaPlayer.prepareAsync();
    }

    private void playMedia() {
        if (!_mediaPlayer.isPlaying()) {
            _mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (_mediaPlayer == null) return;
        if (_mediaPlayer.isPlaying()) {
            _mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (_mediaPlayer.isPlaying()) {
            _mediaPlayer.pause();
            _resumePosition = _mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!_mediaPlayer.isPlaying()) {
            _mediaPlayer.seekTo(_resumePosition);
            _mediaPlayer.start();
        }
    }

    private boolean requestAudioFocus() {
        _audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = _audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                _audioManager.abandonAudioFocus(this);
    }

    public void buildNotification(PlaybackStatus playbackStatus) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setShowWhen(false)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setUsesChronometer(true)
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentTitle(_activeAudio.getTitle())
                .setContentText(_activeAudio.getArtist());

        setNotificationPlaybackState(notificationBuilder, playbackStatus);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }


    private void setNotificationPlaybackState(Notification.Builder builder, PlaybackStatus playbackStatus) {
        if (playbackStatus == PlaybackStatus.PLAYING) {
            builder.setWhen(System.currentTimeMillis() - getCurrentAudioPosition()).setShowWhen(true).setUsesChronometer(true);
        } else {
            builder.setWhen(0).setShowWhen(true).setUsesChronometer(false);
        }

        builder.setOngoing(playbackStatus == PlaybackStatus.PLAYING);
    }

    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _needToAutoResume = true;
            _resumePosition = 0;
            _audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
            if (_audioIndex != -1 && _audioIndex < _audioList.size()) {
                _activeAudio = _audioList.get(_audioIndex);
            } else {
                stopSelf();
            }

            stopMedia();
            _mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
        }
    };

    private BroadcastReceiver pauseAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _needToAutoResume = false;
            pauseMedia();
        }
    };

    private BroadcastReceiver resumeAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _needToAutoResume = true;
            resumeMedia();
        }
    };

    private void callStateListener() {
        _telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        _phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (_mediaPlayer != null) {
                            pauseMedia();
                            _resumePosition = _mediaPlayer.getCurrentPosition();
                            _ongoingCall = true;
                            buildNotification(PlaybackStatus.PAUSED);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (_mediaPlayer != null) {
                            if (_ongoingCall) {
                                _ongoingCall = false;
                                if (_needToAutoResume) {
                                    resumeMedia();
                                    buildNotification(PlaybackStatus.PLAYING);
                                }
                            }
                        }
                        break;
                }
            }
        };

        _telephonyManager.listen(_phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void initMediaSession() {
        if (_mediaSessionManager != null) return; //mediaSessionManager exists

        _mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        _mediaSession = new MediaSessionCompat(getApplicationContext(), "MusicPlayer");
        //Get MediaSessions transport controls
        _transportControls = _mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        _mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        _mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();
    }

    private void updateMetaData() {
        _mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, _activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, _activeAudio.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, _activeAudio.getTitle())
                .build());
    }

    private void registerPlayNewAudio() {
        IntentFilter filter = new IntentFilter(MainActivity.BROADCAST_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    private void registerPauseAudio() {
        IntentFilter filter = new IntentFilter(MainActivity.BROADCAST_PAUSE_AUDIO);
        registerReceiver(pauseAudio, filter);
    }

    private void registerResumeAudio() {
        IntentFilter filter = new IntentFilter(MainActivity.BROADCAST_RESUME_AUDIO);
        registerReceiver(resumeAudio, filter);
    }
}
