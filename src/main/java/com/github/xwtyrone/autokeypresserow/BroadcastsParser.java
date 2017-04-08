package com.github.xwtyrone.autokeypresserow;

import com.github.xwtyrone.autokeypresserow.gui.MainWindow;
import com.google.api.services.youtube.model.LiveBroadcast;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Afecto on 02/04/2017.
 */
public class BroadcastsParser {

    private static MainWindow gui = MainWindow.getInstance();
    private static HashMap<LiveBroadcast,String> map;

    public static void parseAndAddToDropdown(List<LiveBroadcast> list) {
        map = new HashMap<>(list.size());
        int i = 0;
        gui.getBroadcastListBox().removeAllItems();
        if (list.size() == 0) {
            return;
        }

        for (LiveBroadcast broadcast: list) {
            String temp = getFormattedItemString(broadcast);
            map.put(broadcast, temp);
            gui.getBroadcastListBox().addItem(temp);
        }
    }

    @NotNull
    private static String getFormattedItemString(LiveBroadcast item) {
        StringBuilder builder = new StringBuilder();
        builder.append(item.getSnippet().getTitle());
        builder.append(", ID: ");
        builder.append(item.getId());
        builder.append(", Scheduled Start Time: ");
        builder.append(item.getSnippet().getScheduledStartTime());

        builder.trimToSize();
        return builder.toString();
    }
}
