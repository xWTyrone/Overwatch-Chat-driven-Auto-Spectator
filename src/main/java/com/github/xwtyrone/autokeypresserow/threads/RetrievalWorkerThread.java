package com.github.xwtyrone.autokeypresserow.threads;

import com.github.xwtyrone.autokeypresserow.Main;
import com.github.xwtyrone.autokeypresserow.net.YTCommunication;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;


/**
 * Created by pauloafecto on 28/02/2017.
 */
public class RetrievalWorkerThread implements Runnable {

    private static LocalDateTime lastMessageTime = LocalDateTime.parse("2000-01-01T00:00:00");
    private static ExecutorService executor = new ForkJoinPool();
    private static boolean interrupted = false;
    private static String pageToken = null;
    private static boolean firstLoop = true;

    @Override
    public void run() {

        do {

            List<LiveChatMessage> responseList;
            LiveChatMessageListResponse current;
            long waitPeriod;

            current = YTCommunication.retrieveMessages(pageToken);
            responseList = current.getItems();
            waitPeriod = current.getPollingIntervalMillis();

            System.out.println("Received Messages from Server, parsing...");
            System.out.println("Retrieved messages using token: " + pageToken);

            pageToken = current.getNextPageToken();

            // TODO: Make Parser Thread into a loop
            DataParser.setStartList(responseList);
            executor.execute(new DataParser());

            // activates vote counting
            if (firstLoop) {
                Main.activateScheduled();
                firstLoop = false;
            }

            try {
                executor.awaitTermination(waitPeriod, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                interrupted = true;
            }


        } while (!interrupted);
    }



    public static LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public static synchronized void setLastMessageTime(LocalDateTime time) {
        lastMessageTime = time;
    }

}
