package com.github.xwtyrone.autokeypresserow.threads;

import com.github.xwtyrone.autokeypresserow.Main;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;


/**
 * Created by pauloafecto on 28/02/2017.
 */
public class MonitorThread implements Runnable {

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

            current = retrieveMessages(pageToken);
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

    private static LiveChatMessageListResponse retrieveMessages(String pageToken) {
        try {
            String liveChatId = Main.monitoredBroadcast.getSnippet().getLiveChatId();

            System.out.println("Retrieved Live Chat ID: " + liveChatId);

            YouTube.LiveChatMessages.List liveMessagesRequest;
            liveMessagesRequest = Main.youtube.liveChatMessages().list(liveChatId, "snippet");

            if (pageToken != null) {
                liveMessagesRequest.setPageToken(pageToken);
            }
            return liveMessagesRequest.execute();




        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        throw new InternalError(" Unknown error retrieving data ");
    }
}
