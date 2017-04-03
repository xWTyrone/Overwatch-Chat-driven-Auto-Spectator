package com.github.xwtyrone.autokeypresserow;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by pauloafecto on 28/02/2017.
 */
@ThreadSafe
public class VoteCounter {

    private volatile HashMap<VoteCandidates,VoteCount> currentVotes;
    private static VoteCounter Instance = null;
    private static Object instanceLock = new Object();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true); // fair lock so that FIFO and priority

    private VoteCounter() {
         currentVotes = new HashMap<>(VoteCandidates.values().length);
         Arrays.stream(VoteCandidates.values())
                 .forEach(s -> currentVotes.putIfAbsent(s, new VoteCount()));
    }

    public synchronized static VoteCounter getInstance() {
        if (Instance == null) {
            //TODO: make this use java.util.concurrency
            // Only one thread may enter this execution path at any given time
            synchronized (instanceLock) {
                Instance = new VoteCounter();
            }
            return Instance;
        }
        else {
            return Instance;
        }
    }

    public void vote(VoteCandidates candidate) {
        try {
            lock.writeLock().lock();
            currentVotes.get(candidate).addVote();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getVoteCountForCandidate(VoteCandidates candidate) {
        try {
            lock.readLock().lock();
            return currentVotes.get(candidate).getCount();
        } finally {
            lock.readLock().unlock();
        }

    }

    public void reset(VoteCandidates candidate) {
        try {
            lock.writeLock().lock();
            currentVotes.get(candidate).reset();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public VoteCandidates getVotingWinner() {
        try {
            lock.readLock().lock();
            Optional<VoteCandidates> value;
            value = currentVotes.keySet().stream()
                    .reduce((s,t) -> (currentVotes.get(s).getCount() >= currentVotes.get(t).getCount()) ? s : t);
            if (value.isPresent()) {
                return value.get();
            }
            else {
                throw new InternalError();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public void resetAll() {
        lock.writeLock().lock();
        currentVotes.values()
                .forEach(VoteCount::reset);
        lock.writeLock().unlock();
    }



    class VoteCount {
        // uses volatile to make sure threads work with the last value,
        // not their cache value which is different from the modifications made by another thread.
        private volatile int count = 0;
        private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public int getCount() {
            int temp;
            try {
                lock.readLock().lock();
                temp = count;
                return temp;
            } finally {
                lock.readLock().unlock();
            }
        }

        public void reset() {
            try {
                lock.writeLock().lock();
                count = 0;
            } finally {
                lock.writeLock().unlock();
            }

        }

        public void addVote() {
            try {
                lock.writeLock().lock();
                count++;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }


}
