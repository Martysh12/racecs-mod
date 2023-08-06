package com.martysh12.racecs.net;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.martysh12.racecs.RaceCS;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class APIUtils {
    public static final String URL_WEBSOCKET = "wss://aircs.racing/ws";
    public static final String URL_STATIONS = "https://aircs.racing/api/stations";
    public static final String URL_PLAYERS = "https://aircs.racing/api/users";

    public static @Nullable Map<String, String> getStations() {
        HttpResponse<String> response = HTTP.Request.get(URL_STATIONS)
                .header("Accept-Language", "en-US")
                .send();

        if (response.statusCode() != 200) {
            RaceCS.logger.error("Couldn't download stations (status code {}), body: {}", response.statusCode(), response.body());
            return null;
        }

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

        // Time for the boring part
        Map<String, String> stations = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()){
            stations.put(entry.getKey(), entry.getValue().getAsJsonObject().get("name").getAsString());
        }

        return stations;
    }
}
