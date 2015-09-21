package com.m3rc.crf;

/**
 * Created by antonello on 21/09/15.
 */
import javafx.application.Platform;

public abstract class AsyncTask {

    private boolean daemon = true;

    public abstract void onPreExecute();

    public abstract void doInBackground();

    public abstract void onPostExecute();

    public abstract void progressCallback(Object... params);

    public void publishProgress(final Object... params) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                progressCallback(params);
            }
        });
    }

    private final Thread backGroundThread = new Thread(new Runnable() {

        @Override
        public void run() {

            doInBackground();

            Platform.runLater(new Runnable() {

                @Override
                public void run() {

                    onPostExecute();
                }
            });
        }
    });

    public void execute() {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                onPreExecute();

                backGroundThread.setDaemon(daemon);
                backGroundThread.start();
            }
        });
    }

    public void setDaemon(boolean daemon) {

        this.daemon = daemon;
    }

    public void interrupt() {

        this.backGroundThread.interrupt();
    }
}
