package com.github.xwtyrone.autokeypresserow;

import com.github.xwtyrone.autokeypresserow.gui.MainWindow;
import com.github.xwtyrone.autokeypresserow.net.YTCommunication;
import com.github.xwtyrone.autokeypresserow.threads.RetrievalWorkerThread;
import com.github.xwtyrone.autokeypresserow.threads.RobotThread;
import com.github.xwtyrone.autokeypresserow.threads.VoteWinnerCounterThread;
import com.google.api.services.youtube.model.LiveBroadcast;

import java.io.IOException;
import java.util.concurrent.*;

public class Main {

    public static LiveBroadcast monitoredBroadcast;
    private static ScheduledThreadPoolExecutor timerExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() - 1);
    private static BlockingQueue<Runnable> executionQueue = new LinkedBlockingQueue<>();
    private static ThreadPoolExecutor singleExecutor =
            new ThreadPoolExecutor(2, Runtime.getRuntime().availableProcessors(), 100000, TimeUnit.MILLISECONDS, executionQueue);

    public static void main(String[] args) throws IOException {
        // we only want to force init stuff beforehand
        MainWindow.startGUI();
        SystemConfig.getInstance();
        RobotThread.setupRobot();

        YTCommunication.initYTComms();
        BroadcastsParser.parseAndAddToDropdown(YTCommunication.getLiveBroadcasts());

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

        // singleExecutor.execute(new RetrievalWorkerThread());

    }



    public static void activateScheduled() {
        timerExecutor.scheduleWithFixedDelay(new VoteWinnerCounterThread(), 10, 10, TimeUnit.SECONDS);
    }

    public static void activateRobot() {
        timerExecutor.scheduleWithFixedDelay(new RobotThread(), 10, 10, TimeUnit.SECONDS);
    }
}

