package com.martysh12.racecs.gui.toast;

import com.martysh12.racecs.RaceCS;
import com.martysh12.racecs.net.RaceCSWebsocketClient;
import com.martysh12.racecs.net.StationManager;
import com.martysh12.racecs.net.Team;
import com.martysh12.racecs.net.TeamManager;
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
                toastDescription = Text.translatable("toast.collision.desc.you", isPlayer1LocalPlayer ? player2 : player1);
            else
                toastDescription = Text.translatable("toast.collision.desc.other", player1, player2);

            toastManager.add(new RaceToast(
                    Text.translatable("toast.collision.title"),
                    toastDescription,
                    RaceToast.Background.RED,
                    RaceToast.Icon.COLLISION,
                    RaceToast.TitleColor.RED
            ));
        }

        @Override
        public void onVisitation(String user, UUID uuid, String station, String team) {
            RaceCS.logger.info("Player {} from team {} has visited station {}", user, team, station);

            String stationFullName = StationManager.getStationFullName(station);

            Text toastTitle;
            Text toastDescription;
            RaceToast.Icon toastIcon;
            RaceToast.TitleColor titleColor;
            RaceToast.Background toastBackground = RaceToast.Background.GRAY;

            if (team == null) {
                toastTitle = Text.translatable("toast.arrival_single.title");

                // Team race
                if (isLocalPlayerName(user)) {
                    toastDescription = Text.translatable("toast.arrival_single.desc.you", stationFullName);
                    toastIcon = RaceToast.Icon.CHECKMARK;
                    titleColor = RaceToast.TitleColor.GREEN;
                } else {
                    toastDescription = Text.translatable("toast.arrival_single.desc.other", user, stationFullName);
                    toastIcon = RaceToast.Icon.ARRIVAL;
                    titleColor = RaceToast.TitleColor.YELLOW;
                }

                // Non-team race
                if (TeamManager.getNumberOfTeams() == 0) {
                    toastBackground = RaceToast.Background.GREEN;
                }
            } else {
                // Team race
                toastTitle = Text.translatable("toast.arrival_team.title");
                toastIcon = RaceToast.Icon.CHECKMARK;
                titleColor = RaceToast.TitleColor.GREEN;

                Team t = TeamManager.getTeamById(team);
                String fullTeamName = t == null ? Text.translatable("toast.unknown_team").getString() : t.name;

                if (isLocalPlayerName(user)) {
                    toastDescription = Text.translatable("toast.arrival_team.desc.you", stationFullName);
                } else if (isLocalPlayerOnTeam(team)) {
                    toastDescription = Text.translatable("toast.arrival_team.desc.team", user, stationFullName);
                } else {
                    toastDescription = Text.translatable("toast.arrival_team.desc.other", user, stationFullName, fullTeamName);
                    toastIcon = RaceToast.Icon.ARRIVAL;
                    titleColor = RaceToast.TitleColor.YELLOW;
                }

                toastBackground = RaceToast.Background.GREEN;
            }

            toastManager.add(new RaceToast(
                    toastTitle,
                    toastDescription,
                    toastBackground,
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
                toastTitle = Text.translatable("toast.completion.title.you");
                toastDescription = Text.translatable("toast.completion.desc.you", place);
                titleColor = RaceToast.TitleColor.GREEN;
            } else {
                toastTitle = Text.translatable("toast.completion.title.other");
                toastDescription = Text.translatable("toast.completion.desc.other", username, place);
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

        @Override
        public void onCompletionPartial(String player, String team, String teamId, int remaining) {
            RaceCS.logger.info("Player {} from team {} has partially completed the race with #{} team members remaining.", player, team, remaining);

            Text toastTitle;
            Text toastDescription;
            RaceToast.TitleColor titleColor;

            if (isLocalPlayerName(player)) {
                toastTitle = Text.translatable("toast.completion_partial.title.you");
                toastDescription = Text.translatable("toast.completion_partial.desc.you", remaining);
                titleColor = RaceToast.TitleColor.GREEN;
            } else if (isLocalPlayerOnTeam(teamId)) {
                toastTitle = Text.translatable("toast.completion_partial.title.team");
                toastDescription = Text.translatable("toast.completion_partial.desc.team", player, remaining);
                titleColor = RaceToast.TitleColor.GREEN;
            } else {
                toastTitle = Text.translatable("toast.completion_partial.title.other");
                toastDescription = Text.translatable("toast.completion_partial.desc.other", player, remaining, team);
                titleColor = RaceToast.TitleColor.YELLOW;
            }

            toastManager.add(new RaceToast(
                    toastTitle,
                    toastDescription,
                    RaceToast.Background.BLUE,
                    RaceToast.Icon.TEAM_PARTIAL_COMPLETION,
                    titleColor
            ));
        }

        @Override
        public void onCompletionTeam(String player, String team, String teamId, int place) {
            RaceCS.logger.info("Player {} from team {} has completed the race in #{}", player, team, place);

            Text toastTitle;
            Text toastDescription;
            RaceToast.TitleColor titleColor;

            if (isLocalPlayerName(player)) {
                toastTitle = Text.translatable("toast.completion_team.title.you");
                toastDescription = Text.translatable("toast.completion_team.desc.you", place);
                titleColor = RaceToast.TitleColor.GREEN;
            } else if (isLocalPlayerOnTeam(teamId)) {
                toastTitle = Text.translatable("toast.completion_team.title.team");
                toastDescription = Text.translatable("toast.completion_team.desc.team", player, place);
                titleColor = RaceToast.TitleColor.GREEN;
            } else {
                toastTitle = Text.translatable("toast.completion_team.title.other");
                toastDescription = Text.translatable("toast.completion_team.desc.other", player, team, place);
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

    private static boolean isLocalPlayerOnTeam(String team) {
        return RaceCS.mc.player != null && TeamManager.isPlayerInTeam(RaceCS.mc.player.getName().getString(), team);
    }

    public RaceCSWebsocketClient.EventListener getEventListener() {
        return eventListener;
    }
}
