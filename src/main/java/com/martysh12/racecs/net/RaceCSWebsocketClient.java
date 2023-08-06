package com.martysh12.racecs.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.martysh12.racecs.RaceCS;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RaceCSWebsocketClient extends WebSocketClient {
    private final List<EventListener> eventListeners = new ArrayList<>();

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
        ClientPlayerEntity player = RaceCS.mc.player;
        if (player != null && !Objects.equals(type, "ping"))
            player.sendMessage(new LiteralText(message), false);

        switch (type) {
            case "ping" -> {
                for (EventListener eventListener : eventListeners)
                    eventListener.onPing();
            }
            case "visitation" -> {
                for (EventListener eventListener : eventListeners)
                    eventListener.onVisitation(json.get("user").getAsString(), UUID.fromString(json.get("uuid").getAsString()), json.get("station").getAsString());
            }
            case "newPlayer" -> {
                for (EventListener eventListener : eventListeners)
                    eventListener.onNewPlayer(json.get("user").getAsString(), UUID.fromString(json.get("uuid").getAsString()));
            }
            case "removePlayer" -> {
                for (EventListener eventListener : eventListeners)
                    eventListener.onRemovePlayer(json.get("user").getAsString());
            }
            case "stationChange" -> {
                for (EventListener eventListener : eventListeners)
                    eventListener.onStationChange();
            }
            case "completion" -> {
                for (EventListener eventListener : eventListeners)
                    eventListener.onCompletion(json.get("username").getAsString(), json.get("place").getAsInt());
            }
            default -> RaceCS.logger.warn("Unknown message type \"{}\", with message {}", type, message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        RaceCS.logger.error("RaceCS connection closed by " + (remote ? "remote" : "client") + " (code " + code + "), reason: " + reason);

        for (EventListener eventListener : eventListeners)
            eventListener.onDisconnect(code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(EventListener eventListener) {
        eventListeners.remove(eventListener);
    }

    public interface EventListener {
        default void onDisconnect(int code, String reason, boolean remote) {}

        default void onPing() {}
        default void onVisitation(String user, UUID uuid, String station) {}
        default void onNewPlayer(String user, UUID uuid) {}
        default void onRemovePlayer(String user) {}
        default void onStationChange() {}
        default void onCompletion(String username, int place) {}
    }
}
