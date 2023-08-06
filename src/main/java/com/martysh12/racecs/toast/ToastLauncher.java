package com.martysh12.racecs.toast;

import com.martysh12.racecs.RaceCS;
import com.martysh12.racecs.net.RaceCSWebsocketClient;
import com.martysh12.racecs.net.StationManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.LiteralText;

import java.util.UUID;

public class ToastLauncher {
    private final ToastManager toastManager = RaceCS.mc.getToastManager();

    private final RaceCSWebsocketClient.EventListener eventListener = new RaceCSWebsocketClient.EventListener() {
        @Override
        public void onDisconnect(int code, String reason, boolean remote) {
            toastManager.add(new SystemToast(
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    new LiteralText("Websocket Disconnected"),
                    new LiteralText("Please restart Minecraft")
            ));
        }

        @Override
        public void onVisitation(String user, UUID uuid, String station) {
            RaceCS.logger.info("Player {} visited station {}", user, station);
            toastManager.add(new SystemToast(
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    new LiteralText("Visit"),
                    new LiteralText(user + " has reached " + StationManager.getStationFullName(station) + ".")
            ));
        }

        @Override
        public void onNewPlayer(String user, UUID uuid) {
            RaceCS.logger.info("Player {} has been added to the race", user);
        }

        @Override
        public void onRemovePlayer(String user) {
            RaceCS.logger.info("Player {} has been removed from the race", user);
        }

        @Override
        public void onCompletion(String username, int place) {
            RaceCS.logger.info("Player {} has completed the race with place {}", username, place);
            toastManager.add(new SystemToast(
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    new LiteralText("Completion"),
                    new LiteralText("Player " + username + " has won the race with " + ordinal(place) + " place!")
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

    public RaceCSWebsocketClient.EventListener getEventListener() {
        return eventListener;
    }
}
