package com.martysh12.racecs.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Team {
    public String name;
    public String id;
    public List<String> players = new ArrayList<>();

    public static Team fromJsonObject(JsonObject jsonObject) {
        Team team = new Team();
        team.name = jsonObject.get("name").getAsString();
        team.id = jsonObject.get("id").getAsString();

        jsonObject.get("players").getAsJsonArray().forEach(jsonElement -> team.players.add(jsonElement.getAsString()));

        return team;
    }
}
