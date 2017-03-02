package com.github.xwtyrone.autokeypresserow;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by pauloafecto on 02/03/2017.
 */
public enum VoteCandidates {
    F1("1", KeyEvent.VK_F1),
    F2("2", KeyEvent.VK_F2),
    F3("3", KeyEvent.VK_F3),
    F4("4", KeyEvent.VK_F4),
    F5("5", KeyEvent.VK_F5),
    F6("6", KeyEvent.VK_F6),
    F7("7", KeyEvent.VK_F7),
    F8("8", KeyEvent.VK_F8),
    F9("9", KeyEvent.VK_F9),
    F10("10", KeyEvent.VK_F10),
    F11("11", KeyEvent.VK_F11),
    F12("12", KeyEvent.VK_F12),
    O("O", KeyEvent.VK_O);

    private final String id;
    private final int key;
    private static volatile HashMap<MapWrapper, VoteCandidates> Map = new HashMap<>(VoteCandidates.values().length);
    private static ReentrantReadWriteLock MapLock = new ReentrantReadWriteLock();

    VoteCandidates(String id, int key) {
        this.id = id;
        this.key = key;
    }

    // Please only run this once.
    static {
            initMap();
    }

    public String id() {
        return id;
    }

    public int key() {
        return key;
    }

    // The two map methods are complex to put into one singular method without having to wrap around Java type checking
    // TODO: Low Priority - Make mapping into one function.

    public static VoteCandidates map(Integer i) {
        final VoteCandidates[] mapped = new VoteCandidates[1];

        try {
            MapLock.readLock().lock();
            Map.keySet().stream()
                    .forEach(s -> {
                        if (new MapWrapper(i).equals(s)) {
                            mapped[0] = Map.get(s);
                        }
                    });
            return mapped[0];
        } finally {
            MapLock.readLock().unlock();
        }
    }


    public static VoteCandidates map(String i) {
        final VoteCandidates[] mapped = new VoteCandidates[1];

        try {
            MapLock.readLock().lock();
            Map.keySet().stream()
                    .forEach(s -> {
                        if (new MapWrapper(i).equals(s)) {
                            mapped[0] = Map.get(s);
                        }
                    });
            return mapped[0];
        } finally {
            MapLock.readLock().unlock();
        }
    }


    private static void initMap() {
        // runs through all possibilities for vote candidates and adds them to the HashMap
        try {
            MapLock.writeLock().lock();
            Arrays.stream(VoteCandidates.values())
                    .forEach(s -> {
                        try {
                            Map.put(new MapWrapper((Integer) Integer.parseInt(s.id)), s);
                        } catch (Exception e) {
                            Map.put(new MapWrapper(s.id), s);
                        }
                    });
        } finally {
            MapLock.writeLock().unlock();
        }
    }


    static class MapWrapper {

        private enum WrapperType { STRING, INTEGER }

        private WrapperType wrapperType;
        private String s;
        private Integer i;

        private MapWrapper(String s) {
            this.s=s;
            wrapperType = WrapperType.STRING;
        }

        private MapWrapper(Integer i) {
            this.i=i;
            wrapperType = WrapperType.INTEGER;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (!(this.getClass() == o.getClass())){
                return false;
            }

            MapWrapper temp = (MapWrapper) o;

            if (!(this.wrapperType == temp.wrapperType)){
                return false;
            }

            switch (this.wrapperType) {
                case INTEGER:
                    if (this.i.equals(temp.i)){
                        return true;
                    } else {
                        return false;
                    }
                case STRING:
                    if (this.s.equals(temp.s)) {
                        return true;
                    } else {
                        return false;
                    }
            }
            // this should never be run if the code is done properly. It exists so that the Compiler stops complaining
            // about missing return statements
            throw new InternalError();
        }

        public Object unwrap()  {
            if (this.wrapperType == WrapperType.STRING) {
                return s;
            }
            if (this.wrapperType == WrapperType.INTEGER) {
                return i;
            } else {
                throw new InternalError();
            }

        }
    }
}
