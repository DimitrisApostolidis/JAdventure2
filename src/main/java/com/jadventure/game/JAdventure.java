package com.jadventure.game;

import java.util.Timer;
import java.util.TimerTask;
import com.jadventure.game.menus.MainMenu;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JAdventure {
    int secondsPassed = 0;
    Timer myTimer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            secondsPassed++;
            if(secondsPassed == 60) {
                System.out.println("Seconds passed: " + secondsPassed);
            }
            if (secondsPassed == 12){
                System.out.println("Game over");
                System.exit(0);
            }

        }
    };
    public void start(){
        myTimer.scheduleAtFixedRate(task,1000,1000);
    }
    private static Logger logger = LoggerFactory.getLogger(JAdventure.class);

    public static void main(String[] args) {
        JAdventure jAdventureProject = new JAdventure();
        jAdventureProject.start();
        logger.info("Starting JAdventure " + toString(args));
        GameModeType mode = getGameMode(args);
        logger.debug("Starting in mode " + mode.name());
        String serverName = "localhost";
        int port = 4044;
        if (mode == GameModeType.SERVER) {
            port = Integer.parseInt(args[1]);
        } else if (mode == GameModeType.CLIENT) {
            serverName = args[2];
            port = Integer.parseInt(args[1]);
        }
        if (GameModeType.CLIENT == mode) {
            new Client(serverName, port);
        } else if (GameModeType.SERVER == mode) {
            while (true) {
            	ServerSocket listener = null;
                try {
                    listener = new ServerSocket(port);
                    while (true) {
                        Socket server = listener.accept();
                        Runnable r = new MainMenu(server, mode);
                        new Thread(r).start();
                    }
                } catch (IOException c) {
                    c.printStackTrace();
                } finally {
                	try {
						listener.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
            }
        } else {
            QueueProvider.startMessenger(GameModeType.STAND_ALONE);
            new MainMenu();
        }
    }

    private static GameModeType getGameMode(String[] args) {
        if (args == null || args.length == 0 || "".equals(args[0].trim())) {
            return GameModeType.STAND_ALONE;
        }

        try {
            return GameModeType.valueOf(args[0].toUpperCase());
        }
        catch (IllegalArgumentException iae) {
            logger.warn("No game mode '" + args[0].toUpperCase() + "' known." +
                    "Terminating application.");
            System.exit(-1);
        }
        return GameModeType.STAND_ALONE;
    }

    private static String toString(String[] args) {
        if (args.length == 0) {
            return "";
        }

        final StringBuilder bldr = new StringBuilder();
        bldr.append("[ ");
        for (int index = 0; index < args.length; index++) {
            if (index > 0) {
                bldr.append(", ");
            }
            bldr.append(args[index]);
        }
        bldr.append(" ]");
        return bldr.toString();
    }
}
