package com.github.xwtyrone.autokeypresserow;

/**
 * Created by pauloafecto on 02/03/2017.
 */
public class VoteWinnerCounterThread implements Runnable {

    @Override
    public void run() {
        // this thread collects voting results and queues the result for execution.

        VoteCandidates winner;

        winner = VoteCounter.getInstance().getVotingWinner();

        KeyExecutionQueue.getQueue().add(winner);
    }
}
