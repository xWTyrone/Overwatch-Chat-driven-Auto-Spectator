package com.github.xwtyrone.autokeypresserow.net;

import com.github.xwtyrone.autokeypresserow.Main;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by Afecto on 03/04/2017.
 */
public class YTCommunication {


    private volatile static YouTube youtube;

    public synchronized static List<LiveBroadcast> getLiveBroadcasts() {
        try {
            YouTube.LiveBroadcasts.List liveBroadcastRequest = youtube.liveBroadcasts().list("id,snippet");
            liveBroadcastRequest.setBroadcastStatus("all");

            LiveBroadcastListResponse response = liveBroadcastRequest.execute();
            return response.getItems();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("ERROR: See details above");
        }
        throw new InternalError();
    }

    public synchronized static void initYTComms() {
        Credential credential = Auth.authorize();
        System.out.println("Authorization complete!");
        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName("Auto-key-pressing-ow-spectator").build();
        System.out.println("App ready to communicate with Youtube, starting...");
    }

    public synchronized static LiveChatMessageListResponse retrieveMessages(String pageToken) {
        try {
            String liveChatId = Main.monitoredBroadcast.getSnippet().getLiveChatId();

            System.out.println("Retrieved Live Chat ID: " + liveChatId);

            YouTube.LiveChatMessages.List liveMessagesRequest;
            liveMessagesRequest = youtube.liveChatMessages().list(liveChatId, "snippet");

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
