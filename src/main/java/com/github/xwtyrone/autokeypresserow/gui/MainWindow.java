package com.github.xwtyrone.autokeypresserow.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private StyledDocument document = output.getStyledDocument();
    private static PrintStream standardWriter = System.out;
    private PrintStream guiWriter = new PrintStream(new RedirectedOutputStream(), true);

    public MainWindow() {
        broadcastList.addActionListener((ActionEvent e) -> {
            // broadcastList.getSelectedItem()
        });
        refreshButton.addActionListener((ActionEvent e) -> {
            
        });
    }

    public static void startGUI(String[] args) {
        JFrame frame = new JFrame("MainWindow");
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

    public static MainWindow getInstance() {
        return instance;
    }

    public JComboBox getBroadcastListBox() {
        return broadcastList;
    }
}
