package com.github.xwtyrone.autokeypresserow;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Afecto on 28/03/2017.
 */
public class KeyExecutionQueue {

    private static ConcurrentLinkedQueue<VoteCandidates> queue;

    private KeyExecutionQueue() {}

    public static ConcurrentLinkedQueue<VoteCandidates> getQueue() {
        return queue;
    }
}
