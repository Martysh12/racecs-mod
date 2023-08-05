package com.martysh12.racecs.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.martysh12.racecs.RaceCS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

public class RaceCSWebsocketClient extends WebSocketClient {
    private EventListener eventListener;

    public RaceCSWebsocketClient(URI uri) throws URISyntaxException {
        super(uri);
        setConnectionLostTimeout(0);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        RaceCS.logger.info("Connected to the RaceCS websocket endpoint successfully");
    }

    @Override
    public void onMessage(String message) {

        // Parse the message
        JsonObject json;
        try {
            json = JsonParser.parseString(message).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            RaceCS.logger.error("Malformed JSON: " + message);
            return;
        }

        // Do stuff
        String type = json.get("type").getAsString();

        // Send a message to the chat
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && !Objects.equals(type, "ping"))
            player.sendMessage(new LiteralText(message), false);

        switch (type) {
            case "ping" -> {
                if (eventListener != null)
                    eventListener.onPing();
            }
            case "visitation" -> {
                String user = json.get("user").getAsString();
                UUID uuid = UUID.fromString(json.get("uuid").getAsString());
                String station = json.get("station").getAsString();

                RaceCS.logger.info("Player " + user + " visited station " + station);

                if (eventListener != null)
                    eventListener.onVisitation(user, uuid, station);
            }
            case "newPlayer" -> {
                String user = json.get("user").getAsString();
                UUID uuid = UUID.fromString(json.get("uuid").getAsString());

                RaceCS.logger.info("Player " + user + " has been added to the race");

                if (eventListener != null)
                    eventListener.onNewPlayer(user, uuid);
            }
            case "removePlayer" -> {
                String user = json.get("user").getAsString();

                RaceCS.logger.info("Player " + user + " has been removed from the race");

                if (eventListener != null)
                    eventListener.onRemovePlayer(user);
            }
            case "stationChange" -> {
                RaceCS.logger.info("Stations have been changed");

                if (eventListener != null)
                    eventListener.onStationChange();
            }
            case "completion" -> {
                String username = json.get("user").getAsString();
                int place = json.get("place").getAsInt();

                RaceCS.logger.info("Player " + username + " has completed the race with " + place + " place");

                if (eventListener != null)
                    eventListener.onCompletion(username, place);
            }
            default -> RaceCS.logger.info("Unhandled command type " + type);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        RaceCS.logger.error("RaceCS connection closed by " + (remote ? "remote" : "client") + " (code " + code + "), reason: " + reason);

        if (eventListener != null)
            eventListener.onDisconnect(code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        RaceCS.logger.error("An error occurred in the RaceCS client: " + ex.getMessage());
    }

    public void setEventListener(EventListener listener) {
        eventListener = listener;
    }

    public interface EventListener {
        void onDisconnect(int code, String reason, boolean remote);

        void onPing();
        void onVisitation(String user, UUID uuid, String station);
        void onNewPlayer(String user, UUID uuid);
        void onRemovePlayer(String user);
        void onStationChange();
        void onCompletion(String username, int place);
    }
}
