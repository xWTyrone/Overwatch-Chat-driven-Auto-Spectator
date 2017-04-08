package com.github.xwtyrone.autokeypresserow.threads;

import com.github.xwtyrone.autokeypresserow.VoteCandidates;
import com.github.xwtyrone.autokeypresserow.VoteCounter;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.LiveChatMessage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pauloafecto on 28/02/2017.
 */
public class DataParser implements Runnable {

    private static List<LiveChatMessage> startList = null;

    @Override
    public void run() {

        if (startList == null) {
            throw new InternalError();
        }

        //TODO: Youtube will send a response even if there is no data for this app. Handle pageTokens and no data in the result set.
        //TODO: Use parallelStream rather that stream. First, make VoteCounter thread-safe. I have no idea how.

        List<LiveChatMessage> currentSet;
        List<String> currentMessageSet;
        ArrayList<LocalDateTime> dates = new ArrayList<>(startList.size());
        //java is picky with the variables for streams, this variable is temporary so that he can stfo
        LocalDateTime tempTime = RetrievalWorkerThread.getLastMessageTime();


        //process all date operations
        currentSet = startList.parallelStream()
                .filter(s -> tempTime.isBefore(
                        processTime(s.getSnippet().getPublishedAt())))
                .collect(Collectors.toList());
        RetrievalWorkerThread.setLastMessageTime(currentSet.parallelStream()
                .map(s -> processTime(s.getSnippet().getPublishedAt()))
                .reduce(tempTime, (a,b) ->
                    // checks if a points to a time before b. If true, returns b.
                    (a.isBefore(b)) ? b : a)
        );

        // collect all messages
        // remove form the list the elements larger than 4 characters

        currentMessageSet = currentSet.parallelStream()
                .map(s -> s.getSnippet().getTextMessageDetails().getMessageText())
                .filter(s -> s.length() <= 4)
                .map((String s) -> org.apache.commons.lang3.StringUtils.rightPad(s, 4))
                .collect(Collectors.toList());


        currentMessageSet.stream()
                .filter(s -> s.matches("!f[1-9][\\s012]"))
                .forEach(s -> {
                    if (s.substring(3).equals(" ")) {
                        VoteCounter.getInstance().vote(VoteCandidates.map(Integer.parseInt(s.substring(2,3))));
                    } else {
                        VoteCounter.getInstance().vote(VoteCandidates.map(Integer.parseInt(s.substring(2))));
                    }
                });
        currentMessageSet.stream()
                .filter(s -> s.matches("!O\\s\\s"))
                .forEach(s -> VoteCounter.getInstance().vote(VoteCandidates.map(s.substring(1,2))));
    }

    public static LocalDateTime processTime(DateTime value) {
        return LocalDateTime.parse(value.toString().substring(0, value.toString().length() - 1));
    }

    public static void setStartList(List<LiveChatMessage> list) {
        startList = list;
    }
}
