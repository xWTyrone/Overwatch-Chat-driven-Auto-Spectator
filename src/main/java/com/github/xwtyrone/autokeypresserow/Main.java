package com.github.xwtyrone.autokeypresserow;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by pauloafecto on 28/02/2017.
 */
public class Main {

    public static YouTube youtube;
    public static LiveBroadcast monitoredBroadcast;
    private static ScheduledThreadPoolExecutor timerExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() - 1);

    public static void main(String[] args) throws IOException {
        // create and connect to youtube api, occurs in method
        List<LiveBroadcast> broadcastList = getLiveBroadcasts();

        System.out.println("Data Returned, parsing...");

        int i = 0;

        if (broadcastList.size() == 0) {
            System.out.println("There are no active livestreams to retrieve.");
            System.exit(1);
        }

        if (broadcastList.size() != 1) {

            System.out.println("More than 1 broadcast is active at the moment. Choose a broadcast below.");
            System.out.println("\n================== Returned Broadcasts ==================\n");
            for (LiveBroadcast broadcast : broadcastList) {
                System.out.println("Broadcast: " + i);
                System.out.println("  - Id: " + broadcast.getId());
                System.out.println("  - Title: " + broadcast.getSnippet().getTitle());
                System.out.println("  - Description: " + broadcast.getSnippet().getDescription());
                System.out.println("  - Published At: " + broadcast.getSnippet().getPublishedAt());
                System.out.println(
                        "  - Scheduled Start Time: " + broadcast.getSnippet().getScheduledStartTime());
                System.out.println(
                        "  - Scheduled End Time: " + broadcast.getSnippet().getScheduledEndTime());
                System.out.println("\n-------------------------------------------------------------\n");
                i++;
            }
            // TODO: Get broadcast selection input from user Implement with GUI?
        }

        LiveBroadcast broadcast = broadcastList.get(i);

        System.out.println("Now monitoring Broadcast " + i + " with ID: " + broadcast.getId());
        System.out.println("Live Chat monitoring starting...");

        if (broadcast.getSnippet().getLiveChatId() == null) {
            System.err.println("ERROR: Could not retrieve Live Chat ID for monitoring. Is the chat active in the stream? \n " +
                    "Please check stream chat availability in the Live Control Room and rerun the application.");
            System.out.println("Application terminated");
            System.exit(2);
        }

        monitoredBroadcast = broadcast;

        new Thread(new MonitorThread()).start();

        timerExecutor.scheduleWithFixedDelay(new VoteExecuteActionThread(), 20, 10, TimeUnit.SECONDS);

        System.out.println("Type quit and enter to exit app.");
      while (true) {
          String s = JOptionPane.showInputDialog("Enter a command: ");

          if (s.equals("quit")) {
              System.exit(0);
          }
      }
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
}
