package com.martysh12.racecs;

import com.martysh12.racecs.net.RaceCSWebsocketClient;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class RaceCS implements ClientModInitializer {
    public static final String MOD_ID = "racecs";
    public static final Logger logger = LoggerFactory.getLogger(MOD_ID);

    public static RaceCSWebsocketClient websocketClient;

    @Override
    public void onInitializeClient() {
        createWebsocketClient();
    }

    private void createWebsocketClient() {
        try {
            websocketClient = new RaceCSWebsocketClient(new URI("wss://aircs.racing/ws"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e); // This shouldn't happen to any wallaby
        }
        websocketClient.connect();
    }
}
