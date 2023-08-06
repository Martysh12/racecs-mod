package com.martysh12.racecs.net;

import com.martysh12.racecs.RaceCS;

import java.util.HashMap;
import java.util.Map;

public class StationManager {
    private static Map<String, String> stations = new HashMap<>();

    public static void downloadStations() {
        new Thread(() -> {
            Map<String, String> stationMap = APIUtils.getStations();
            if (stationMap == null) {
                RaceCS.logger.error("Unable to download the stations. Keeping last map.");
                return;
            }

            stations = stationMap;
            RaceCS.logger.info("Downloaded stations successfully. {} stations loaded.", stationMap.size());
        }, "Station Download Thread").start();
    }

    public static String getStationFullName(String shortName) {
        return stations.get(shortName);
    }
}
