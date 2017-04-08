package com.github.xwtyrone.autokeypresserow.gui;

import com.github.xwtyrone.autokeypresserow.BroadcastsParser;
import com.github.xwtyrone.autokeypresserow.Main;
import com.github.xwtyrone.autokeypresserow.net.YTCommunication;
import com.google.api.services.youtube.model.LiveBroadcast;
import org.jetbrains.annotations.Contract;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * Created by Afecto on 31/03/2017.
 */
public class MainWindow {
    private static MainWindow instance;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextPane output;
    private JComboBox broadcastList;
    private JPanel infoPanel1;
    private JPanel infoPanel2;
    private JButton refreshButton;
    private JLabel broadcastID;
    private JLabel broadcastTitle;
    private JButton selectButton;
    private StyledDocument document = output.getStyledDocument();
    private static PrintStream standardWriter = System.out;
    private PrintStream guiWriter = new PrintStream(new RedirectedOutputStream(), true);
    private static JFrame frame = new JFrame("MainWindow");
    public MainWindow() {
        broadcastList.addActionListener((ActionEvent e) -> {
            updateText();
        });
        refreshButton.addActionListener((ActionEvent e) -> {
            BroadcastsParser.parseAndAddToDropdown(YTCommunication.getLiveBroadcasts());
            repaint();
        });
    }

    public static void startGUI() {
        instance = new MainWindow();
        frame.setContentPane(instance.panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    {
        System.setOut(guiWriter);
    }

    private class RedirectedOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            try {
                document.insertString(document.getLength(), String.valueOf((char) b) ,null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    @Contract(pure = true)
    public static MainWindow getInstance() {
        return instance;
    }

    public JComboBox getBroadcastListBox() {
        return broadcastList;
    }

    private void updateText() {
        broadcastID.setText(item.getId());
        broadcastTitle.setText(item.getSnippet().getTitle());
        infoPanel1.revalidate();
        infoPanel1.repaint();
        frame.pack();
    }

    public void repaint() {
        panel1.revalidate();
        panel1.updateUI();
        panel1.repaint();
        frame.pack();
    }
}
