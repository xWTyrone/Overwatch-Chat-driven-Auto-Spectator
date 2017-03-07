package com.github.xwtyrone.autokeypresserow;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by pauloafecto on 02/03/2017.
 */
public class VoteExecuteActionThread implements Runnable {

    @Override
    public void run() {
        // this thread collects voting results and queues the result for execution.

        Optional<VoteCandidates> winner;

        winner = Arrays.stream(VoteCandidates.values())
                .reduce((s,t) -> (VoteCounter.getInstance().getVoteCountForCandidate(s) >= VoteCounter.getInstance().getVoteCountForCandidate(t)) ? s : t);

        if (!winner.isPresent()) {
            throw new InternalError();
        }
    }
}
