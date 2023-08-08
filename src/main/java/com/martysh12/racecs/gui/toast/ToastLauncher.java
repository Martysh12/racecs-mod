package com.martysh12.racecs.gui.toast;

import com.martysh12.racecs.RaceCS;
import com.martysh12.racecs.net.RaceCSWebsocketClient;
import com.martysh12.racecs.net.StationManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.UUID;

public class ToastLauncher {
    private final ToastManager toastManager = RaceCS.mc.getToastManager();

    private final RaceCSWebsocketClient.EventListener eventListener = new RaceCSWebsocketClient.EventListener() {
        @Override
        public void onCollision(String player1, String player2) {
            RaceCS.logger.info("Player {} has collided with {}", player1, player2);

            Text toastDescription;
            boolean isPlayer1LocalPlayer = isLocalPlayerName(player1);
            if (isPlayer1LocalPlayer || isLocalPlayerName(player2))
                toastDescription = Text.literal("You've collided with " + (isPlayer1LocalPlayer ? player2 : player1) + "!");
            else
                toastDescription = Text.literal(player1 + " has collided with " + player2 + "!");

            toastManager.add(new RaceToast(
                    Text.literal("Collision"),
                    toastDescription,
                    RaceToast.Background.RED,
                    RaceToast.Icon.COLLISION,
                    RaceToast.TitleColor.RED
            ));
        }

        @Override
        public void onVisitation(String user, UUID uuid, String station) {
            RaceCS.logger.info("Player {} visited station {}", user, station);

            Text toastDescription;
            RaceToast.Icon toastIcon;
            RaceToast.TitleColor titleColor;

            if (isLocalPlayerName(user)) {
                toastDescription = Text.literal("You've arrived at " + StationManager.getStationFullName(station) + ".");
                toastIcon = RaceToast.Icon.CHECKMARK;
                titleColor = RaceToast.TitleColor.GREEN;
            }
            else {
                toastDescription = Text.literal(user + " has arrived at " + StationManager.getStationFullName(station) + ".");
                toastIcon = RaceToast.Icon.ARRIVAL;
                titleColor = RaceToast.TitleColor.YELLOW;
            }

            toastManager.add(new RaceToast(
                    Text.literal("Arrival"),
                    toastDescription,
                    RaceToast.Background.GREEN,
                    toastIcon,
                    titleColor
            ));
        }

        @Override
        public void onCompletion(String username, int place) {
            RaceCS.logger.info("Player {} has completed the race in #{}", username, place);

            Text toastTitle;
            Text toastDescription;
            RaceToast.TitleColor titleColor;

            if (isLocalPlayerName(username)) {
                toastTitle = Text.literal("Congratulations!");
                toastDescription = Text.literal(
                        "You've reached the terminal station in " + ordinal(place) + " place."
                );
                titleColor = RaceToast.TitleColor.GREEN;
            } else {
                toastTitle = Text.literal("Completion");
                toastDescription = Text.literal(username + " has completed the race in " + ordinal(place) + " place!");
                titleColor = RaceToast.TitleColor.YELLOW;
            }

            toastManager.add(new RaceToast(
                    toastTitle,
                    toastDescription,
                    RaceToast.Background.YELLOW,
                    place == 1 ? RaceToast.Icon.FIRST : RaceToast.Icon.TROPHY,
                    titleColor
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

    private static boolean isLocalPlayerName(String username) {
        return RaceCS.mc.player != null && Objects.equals(RaceCS.mc.player.getName().getString(), username);
    }

    public RaceCSWebsocketClient.EventListener getEventListener() {
        return eventListener;
    }
}
