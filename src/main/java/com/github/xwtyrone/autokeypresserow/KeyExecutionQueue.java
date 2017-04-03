package com.github.xwtyrone.autokeypresserow;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Afecto on 28/03/2017.
 */
public class KeyExecutionQueue {

    private static LinkedBlockingQueue<VoteCandidates> queue = new LinkedBlockingQueue<>();

    private KeyExecutionQueue() {}

    public static LinkedBlockingQueue<VoteCandidates> getQueue() {
        return queue;
    }
}
