package com.martysh12.racecs;

import com.martysh12.racecs.net.RaceCSWebsocketClient;
import com.martysh12.racecs.net.StationManager;
import com.martysh12.racecs.net.APIUtils;
import com.martysh12.racecs.gui.toast.ToastLauncher;
import com.martysh12.racecs.net.TeamManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class RaceCS implements ClientModInitializer {
    public static final String ID = "racecs";
    public static final Logger logger = LoggerFactory.getLogger(ID);
    private static final Reconnector reconnector = new Reconnector();
    private static final RaceCSWebsocketClient.EventListener eventListener = new RaceCSWebsocketClient.EventListener() {
        @Override
        public void onDisconnect(int code, String reason, boolean remote) {
            hasDisconnected = true;
        }

        @Override
        public void onStationChange() {
            RaceCS.logger.info("Stations have been changed");
            StationManager.downloadStations();
        }
    };

    private static boolean hasDisconnected = true;

    public static RaceCS INSTANCE;

    public static MinecraftClient mc;
    private static ToastLauncher toastLauncher;
    private static RaceCSWebsocketClient websocketClient;

    @Override
    public void onInitializeClient() {
        // Wait until MinecraftClient initialises (see MinecraftClientMixin.onInit)
        if (INSTANCE == null) {
            INSTANCE = this;
            return;
        }

        RaceCS.logger.info("Initialising {}", ID);

        // Get the Minecraft client, so we don't get it manually each time
        mc = MinecraftClient.getInstance();
        toastLauncher = new ToastLauncher(); // ToastLauncher depends on the line above

        // Set up the websocket stuff
        new Thread(reconnector, "Reconnector Thread").start();
    }

    private static void createWebsocketClient() {
        try {
            websocketClient = new RaceCSWebsocketClient(new URI(APIUtils.URL_WEBSOCKET));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e); // This shouldn't happen to any wallaby
        }
        websocketClient.connect();

        websocketClient.addEventListener(TeamManager.getEventListener());
        websocketClient.addEventListener(toastLauncher.getEventListener());
        websocketClient.addEventListener(eventListener);
    }

    private static class Reconnector implements Runnable {
        @Override
        public void run() {
            while (true) {
                // Re-create the websocket client if it disconnects
                if (hasDisconnected) {
                    hasDisconnected = false;
                    createWebsocketClient();
                }

                try {
                    Thread.sleep(5000); // Shut up IntelliJ
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
