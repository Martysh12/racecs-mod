package com.martysh12.racecs.gui.toast;

import com.martysh12.racecs.RaceCS;
import com.martysh12.racecs.net.RaceCSWebsocketClient;
import com.martysh12.racecs.net.StationManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

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
                toastDescription = new TranslatableText("toast.collision.desc.you", isPlayer1LocalPlayer ? player2 : player1);
            else
                toastDescription = new TranslatableText("toast.collision.desc.other", player1, player2);

            toastManager.add(new RaceToast(
                    new TranslatableText("toast.collision.title"),
                    toastDescription,
                    RaceToast.Background.RED,
                    RaceToast.Icon.COLLISION,
                    RaceToast.TitleColor.RED
            ));
        }

        @Override
        public void onVisitation(String user, UUID uuid, String station) {
            RaceCS.logger.info("Player {} visited station {}", user, station);

            String stationFullName = StationManager.getStationFullName(station);

            Text toastDescription;
            RaceToast.Icon toastIcon;
            RaceToast.TitleColor titleColor;

            if (isLocalPlayerName(user)) {
                toastDescription = new TranslatableText("toast.arrival.desc.you", stationFullName);
                toastIcon = RaceToast.Icon.CHECKMARK;
                titleColor = RaceToast.TitleColor.GREEN;
            }
            else {
                toastDescription = new TranslatableText("toast.arrival.desc.other", user, stationFullName);
                toastIcon = RaceToast.Icon.ARRIVAL;
                titleColor = RaceToast.TitleColor.YELLOW;
            }

            toastManager.add(new RaceToast(
                    new TranslatableText("toast.arrival.title"),
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
                toastTitle = new TranslatableText("toast.completion.title.you");
                toastDescription = new TranslatableText("toast.completion.desc.you", place);
                titleColor = RaceToast.TitleColor.GREEN;
            } else {
                toastTitle = new TranslatableText("toast.completion.title.other");
                toastDescription = new TranslatableText("toast.completion.desc.other", username, place);
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

    private static boolean isLocalPlayerName(String username) {
        return RaceCS.mc.player != null && Objects.equals(RaceCS.mc.player.getName().getString(), username);
    }

    public RaceCSWebsocketClient.EventListener getEventListener() {
        return eventListener;
    }
}
