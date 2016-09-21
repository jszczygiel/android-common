package com.jszczygiel.foundation.helpers;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;

public class MediaPlayerHelper {

    public static final String CONTENT_SETTINGS_SYSTEM_ALARM_ALERT = "content://settings/system/alarm_alert";

    private MediaPlayerHelper() {
    }

    public static void setMediaPlayerDataSource(Context context,
                                                MediaPlayer mp, String fileInfo) throws Exception {

        if (fileInfo.startsWith("content://")) {
            try {
                Uri uri = Uri.parse(fileInfo);
                fileInfo = getRingtonePathFromContentUri(context, uri);
            } catch (Exception e) {
            }
        }

        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                try {
                    setMediaPlayerDataSourcePreHoneyComb(context, mp, fileInfo);
                } catch (Exception e) {
                    setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo);
                }
            } else {
                setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo);
            }

        } catch (Exception e) {
            try {
                setMediaPlayerDataSourceUsingFileDescriptor(context, mp,
                        fileInfo);
            } catch (Exception ee) {
                mp.reset();
                String uri = null;
                if (fileInfo != null && !CONTENT_SETTINGS_SYSTEM_ALARM_ALERT.equals(fileInfo)) {
                    uri = getRingtoneUriFromPath(context, fileInfo);
                    mp.setDataSource(uri);
                }

                if (uri == null || CONTENT_SETTINGS_SYSTEM_ALARM_ALERT.equals(fileInfo)) {
                    mp.setDataSource(context, Uri.parse(CONTENT_SETTINGS_SYSTEM_ALARM_ALERT));

                }

            }
        }
    }

    private static void setMediaPlayerDataSourcePreHoneyComb(Context context,
                                                             MediaPlayer mp, String fileInfo) throws Exception {
        mp.reset();
        mp.setDataSource(fileInfo);
    }

    private static void setMediaPlayerDataSourcePostHoneyComb(Context context,
                                                              MediaPlayer mp, String fileInfo) throws Exception {
        mp.reset();
        mp.setDataSource(context, Uri.parse(Uri.encode(fileInfo)));
    }

    private static void setMediaPlayerDataSourceUsingFileDescriptor(
            Context context, MediaPlayer mp, String fileInfo) throws Exception {
        File file = new File(fileInfo);
        FileInputStream inputStream = new FileInputStream(file);
        mp.reset();
        mp.setDataSource(inputStream.getFD());
        inputStream.close();
    }

    private static String getRingtoneUriFromPath(Context context, String path) {
        Uri ringtonesUri = MediaStore.Audio.Media.getContentUriForPath(path);
        Cursor ringtoneCursor = context.getContentResolver().query(
                ringtonesUri, null,
                MediaStore.Audio.Media.DATA + "='" + path + "'", null, null);
        if (ringtoneCursor != null && ringtoneCursor.moveToFirst()) {

            long id = ringtoneCursor.getLong(ringtoneCursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            ringtoneCursor.close();

            if (!ringtonesUri.toString().endsWith(String.valueOf(id))) {
                return ringtonesUri + "/" + id;
            }
            return ringtonesUri.toString();

        }
        return null;
    }

    public static String getRingtonePathFromContentUri(Context context,
                                                       Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor ringtoneCursor = context.getContentResolver().query(contentUri,
                proj, null, null, null);
        if (ringtoneCursor != null && ringtoneCursor.moveToFirst()) {

            String path = ringtoneCursor.getString(ringtoneCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            ringtoneCursor.close();
            return path;
        }
        return null;
    }

    public static boolean isPhoneSilent(Context context) {
        AudioManager audioService = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean ringerModeSilent = false, streamSilent = false;
        if (audioService.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            ringerModeSilent = true;
        }
        int current = audioService.getStreamVolume(AudioManager.STREAM_ALARM);
        if (current <= 1) {
            streamSilent = true;
        }

        return streamSilent || ringerModeSilent;
    }
}
