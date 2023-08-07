package com.martysh12.racecs.toast;

import com.martysh12.racecs.RaceCS;
import com.martysh12.racecs.net.RaceCSWebsocketClient;
import com.martysh12.racecs.net.StationManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.UUID;

public class ToastLauncher {
    private final ToastManager toastManager = RaceCS.mc.getToastManager();

    private final RaceCSWebsocketClient.EventListener eventListener = new RaceCSWebsocketClient.EventListener() {
        @Override
        public void onDisconnect(int code, String reason, boolean remote) {
            toastManager.add(new SystemToast(
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    new LiteralText("Websocket Disconnected"),
                    new LiteralText("Please restart Minecraft!")
            ));
        }

        @Override
        public void onCollision(String player1, String player2) {
            RaceCS.logger.info("Player {} has collided with {}", player1, player2);

            Text toastDescription;
            boolean player1IsPlayerName = isPlayerName(player1);
            if (player1IsPlayerName || isPlayerName(player2))
                toastDescription = new LiteralText("You've collided with " + (player1IsPlayerName ? player2 : player1) + "!");
            else
                toastDescription = new LiteralText(player1 + " has collided with " + player2 + "!");

            toastManager.add(new SystemToast(
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    new LiteralText("Collision"),
                    toastDescription
            ));
        }

        @Override
        public void onVisitation(String user, UUID uuid, String station) {
            RaceCS.logger.info("Player {} visited station {}", user, station);

            Text toastDescription;
            if (isPlayerName(user))
                toastDescription = new LiteralText("You've arrived at " + StationManager.getStationFullName(station) + ".");
            else
                toastDescription = new LiteralText(user + " has arrived at " + StationManager.getStationFullName(station) + ".");

            toastManager.add(new SystemToast(
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    new LiteralText("Arrival"),
                    toastDescription
            ));
        }

        @Override
        public void onCompletion(String username, int place) {
            RaceCS.logger.info("Player {} has completed the race in #{}", username, place);

            Text toastTitle;
            Text toastDescription;
            if (isPlayerName(username)) {
                toastTitle = new LiteralText("Congratulations!");
                toastDescription = new LiteralText(
                        "You've reached the terminal station in " + ordinal(place) + " place."
                );
            } else {
                toastTitle = new LiteralText("Completion");
                toastDescription = new LiteralText(username + " has completed the race in " + ordinal(place) + " place!");
            }

            toastManager.add(new SystemToast(
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    toastTitle,
                    toastDescription
            ));
        }
    };

    private static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        return switch (i % 100) {
            case 11, 12, 13 -> i + "th";
            default -> i + suffixes[i % 10];
        };
    }

    private static boolean isPlayerName(String username) {
        return RaceCS.mc.player != null && Objects.equals(RaceCS.mc.player.getName().getString(), username);
    }

    public RaceCSWebsocketClient.EventListener getEventListener() {
        return eventListener;
    }
}
