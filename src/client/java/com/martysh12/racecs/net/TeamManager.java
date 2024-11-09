package com.martysh12.racecs.net;

/// [
//    {
//        "name": "The Winning Team",
//        "id": "0",
//        "players": [
//            "Player2",
//            "Player3"
//        ],
//        "visited": [
//            "VIC",
//            "LI"
//        ],
//        "returned": [
//            "Player2",
//            "Player3"
//        ],
//        "place": 1
//    },
//    {
//        "name": "The Losing Team",
//        "id": "1",
//        "players": [
//            "Player0",
//            "Player1"
//        ],
//        "visited": [
//            "LI",
//            "VIC"
//        ],
//        "returned": [
//            "Player1",
//            "Player0"
//        ],
//        "place": 2
//    }
//]

import com.martysh12.racecs.RaceCS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeamManager {
    private static List<Team> teams = new ArrayList<>();
    private static final RaceCSWebsocketClient.EventListener eventListener = new RaceCSWebsocketClient.EventListener() {
        @Override
        public void onTeamRename(String teamId, String name) {
            RaceCS.logger.info("Team ID {} was renamed to {}", teamId, name);

            Team team = getTeamById(teamId);

            if (team != null)
                team.name = name;
        }

        @Override
        public void onTeaming(List<Team> teamList) {
            RaceCS.logger.info("Teaming event, received {} teams", teamList.size());
            teams = teamList;
        }
    };

    public static RaceCSWebsocketClient.EventListener getEventListener() {
        return eventListener;
    }

    public static void downloadTeams() {
        new Thread(() -> {
            RaceCS.logger.info("Starting download of teams");
            List<Team> teamList = APIUtils.getTeams();
            if (teamList == null) {
                RaceCS.logger.error("Unable to download the teams. Keeping last list.");
                return;
            }

            teams = teamList;
            RaceCS.logger.info("Downloaded teams successfully. {} teams loaded.", teams.size());
        }, "Team Download Thread").start();
    }

    public static Team getTeamById(String teamId) {
        for (Team team: teams) {
            if (Objects.equals(team.id, teamId)) {
                return team;
            }
        }

        return null;
    }

    public static Team getTeamByName(String teamName) {
        for (Team team: teams) {
            if (Objects.equals(team.name, teamName)) {
                return team;
            }
        }

        return null;
    }

    public static boolean isPlayerInTeam(String username, String teamName) {
        for (Team team : teams) {
            if (Objects.equals(team.id, teamName) && team.players.contains(username)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasTeamClaimedStation(Team team, String stationShortCode) {
        if (team.visited == null)
            return false;

        return team.visited.contains(stationShortCode);
    }

    public static Team getPlayerTeam(String player) {
        for (Team team : teams) {
            if (team.players.contains(player)) {
                return team;
            }
        }

        return null;
    }

    public static int getNumberOfTeams() {
        return teams.size();
    }
}
