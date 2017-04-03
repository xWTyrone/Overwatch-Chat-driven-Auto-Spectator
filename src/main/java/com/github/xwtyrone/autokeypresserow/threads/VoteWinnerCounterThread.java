package com.github.xwtyrone.autokeypresserow.threads;

import com.github.xwtyrone.autokeypresserow.KeyExecutionQueue;
import com.github.xwtyrone.autokeypresserow.Main;
import com.github.xwtyrone.autokeypresserow.VoteCandidates;
import com.github.xwtyrone.autokeypresserow.VoteCounter;

/**
 * Created by pauloafecto on 02/03/2017.
 */
public class VoteWinnerCounterThread implements Runnable {

    private static boolean firstRun = true;

    @Override
    public void run() {
        // this thread collects voting results and queues the result for execution.

        VoteCandidates winner;

        winner = VoteCounter.getInstance().getVotingWinner();
        VoteCounter.getInstance().resetAll();

        KeyExecutionQueue.getQueue().add(winner);

        if (firstRun) {
            Main.activateRobot();
        }
    }
}
