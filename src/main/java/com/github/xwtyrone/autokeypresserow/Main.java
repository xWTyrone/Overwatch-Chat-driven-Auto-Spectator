package com.github.xwtyrone.autokeypresserow;

import com.github.xwtyrone.autokeypresserow.gui.MainWindow;
import com.github.xwtyrone.autokeypresserow.threads.MonitorThread;
import com.github.xwtyrone.autokeypresserow.threads.RobotThread;
import com.github.xwtyrone.autokeypresserow.threads.VoteWinnerCounterThread;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static YouTube youtube;
    public static LiveBroadcast monitoredBroadcast;
    private static ScheduledThreadPoolExecutor timerExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() - 1);
    private static BlockingQueue<Runnable> executionQueue = new LinkedBlockingQueue<>();
    private static ThreadPoolExecutor singleExecutor =
            new ThreadPoolExecutor(2, Runtime.getRuntime().availableProcessors(), 100000, TimeUnit.MILLISECONDS, executionQueue);

    public static void main(String[] args) throws IOException {
            // we only want to force init stuff beforehand
            SystemConfig.getInstance();
            RobotThread.setupRobot();
            MainWindow.startGUI(null);
            // create and connect to youtube api, occurs in method
            List<LiveBroadcast> broadcastList = getLiveBroadcasts();


            System.out.println("Data Returned, parsing...");

            /*int i = 0;


            LiveBroadcast broadcast = broadcastList.get(i);

            System.out.println("Now monitoring Broadcast " + i + " with ID: " + broadcast.getId());
            System.out.println("Live Chat monitoring starting...");

            if (broadcast.getSnippet().getLiveChatId() == null) {
                System.out.println("ERROR: Could not retrieve Live Chat ID for monitoring. Is the chat active in the stream? \n " +
                        "Please check stream chat availability in the Live Control Room and rerun the application.");
                System.out.println("Application terminated");
            }*/

            //TODO: set the monitored broadcast and check execution path

            singleExecutor.execute(new MonitorThread());

    }

    private static List<LiveBroadcast> getLiveBroadcasts() throws IOException {
        Credential credential = Auth.authorize();
        System.out.println("Authorization complete!");
        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName("Auto-key-pressing-ow-spectator").build();
        System.out.println("App ready to communicate with Youtube, starting...");


        YouTube.LiveBroadcasts.List liveBroadcastRequest = youtube.liveBroadcasts().list("id,snippet");
        liveBroadcastRequest.setBroadcastStatus("all");

        LiveBroadcastListResponse response = liveBroadcastRequest.execute();
        return response.getItems();
    }

    public static void activateScheduled() {
        timerExecutor.scheduleWithFixedDelay(new VoteWinnerCounterThread(), 10, 10, TimeUnit.SECONDS);
    }

    public static void activateRobot() {
        timerExecutor.scheduleWithFixedDelay(new RobotThread(), 10, 10, TimeUnit.SECONDS);
    }
}

