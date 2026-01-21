package com.whosin.app.ui.activites.home.Chat;

import android.media.MediaRecorder;

import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;



    private void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }


    void start(String filePath) throws IOException {
        if (mediaRecorder == null) {
            initMediaRecorder();
        }
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.prepare();
        mediaRecorder.start();
        isRecording = true;
    }

//    void stop() {
//        try {
//            if (isRecording && mediaRecorder != null) {
//                mediaRecorder.stop();
//                isRecording = false;
//            }
//            destroyMediaRecorder();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//
//    }

    void stop() {
        try {
            if (isRecording && mediaRecorder != null) {
                mediaRecorder.stop();
                isRecording = false;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            destroyMediaRecorder();
        }
    }

    private void destroyMediaRecorder() {
        if (mediaRecorder != null) mediaRecorder.release();
//        mediaRecorder.release();
        mediaRecorder = null;
    }

    boolean isRecording() {
        return mediaRecorder != null;
    }
}
