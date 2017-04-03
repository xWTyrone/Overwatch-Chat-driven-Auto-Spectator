package com.github.xwtyrone.autokeypresserow.threads;

import com.github.xwtyrone.autokeypresserow.KeyExecutionQueue;
import com.github.xwtyrone.autokeypresserow.SystemConfig;
import com.github.xwtyrone.autokeypresserow.VoteCandidates;

import java.awt.*;

/**
 * Created by Afecto on 28/03/2017.
 */
public class RobotThread implements Runnable {

    private static Robot robot;


    @Override
    public void run() {
        VoteCandidates candidate = KeyExecutionQueue.getQueue().poll();
        System.out.println("Executing keypress...");
        if (SystemConfig.getInstance().testMode) {
            System.out.println("KEYPRESS EVENT: KEY ID " + candidate.key() + " FOR KEY STRING " + candidate.id());
        }
        else {
            keyPressSequence(candidate.key());
        }
    }

    public static void setupRobot() {
        if (robot == null) {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    private void keyPressSequence(int key) {
        robot.keyPress(key);
        robot.delay(250);
        robot.keyRelease(key);
    }
}
