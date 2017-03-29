package com.github.xwtyrone.autokeypresserow;

import com.sun.jna.Platform;

/**
 * Created by Afecto on 28/03/2017.
 */
public class SystemConfig
{
    private static SystemConfig instance;
    public boolean testMode;

    private SystemConfig() {
        testMode = !(Platform.isWindows());
        if (testMode) {
            System.out.println("Application running in non windows environment, entering test mode");
        }
    }

    public static SystemConfig getInstance() {
        if (instance ==  null) {
            instance = new SystemConfig();
        }
        return instance;
    }
}
