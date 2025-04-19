package com.littlepay.faresystem.service;

import com.littlepay.faresystem.model.Tap;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.ArrayList;

public class TapOrganiser {
    public static Map<String, List<Tap>> groupAndSortTapsByPan(List<Tap> taps) {
        Map<String, List<Tap>> sortedTapsByPanMap = new HashMap<>();
        for(Tap currentTap : taps) {
            List<Tap> currentPanTaps = sortedTapsByPanMap.getOrDefault(currentTap.getPan(), new ArrayList<>());
            currentPanTaps.add(currentTap);
            sortedTapsByPanMap.put(currentTap.getPan(), currentPanTaps);
        }

        for(List<Tap> tapList : sortedTapsByPanMap.values()) {
            tapList.sort(Comparator.comparing(Tap::getDateTimeUTC));
        }

        return sortedTapsByPanMap;
    }
}
