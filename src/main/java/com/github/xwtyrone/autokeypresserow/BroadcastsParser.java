package com.github.xwtyrone.autokeypresserow;

import com.github.xwtyrone.autokeypresserow.gui.MainWindow;
import com.google.api.services.youtube.model.LiveBroadcast;

import java.util.List;

/**
 * Created by Afecto on 02/04/2017.
 */
public class BroadcastsParser {
    private static MainWindow gui = MainWindow.getInstance();

    public static void parseAndAddToDropdown(List<LiveBroadcast> list) {
        int i = 0;

        if (list.size() == 0) {
            return;
        }

        for (LiveBroadcast broadcast: list) {
            gui.getBroadcastListBox().removeAllItems();
            gui.getBroadcastListBox().addItem(broadcast);
        }
    }
}
