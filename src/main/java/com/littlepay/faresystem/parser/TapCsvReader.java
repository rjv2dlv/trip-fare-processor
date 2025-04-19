package com.littlepay.faresystem.parser;

import com.littlepay.faresystem.model.Tap;
import com.littlepay.faresystem.utils.Constants;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TapCsvReader {
    public static List<Tap> getTapsFromFile(String inputFile) throws IOException {
        List<Tap> taps = new ArrayList<>();
        int recordNumber = 0;
        InputStream inputStream = Tap.class.getClassLoader().getResourceAsStream(inputFile);
        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found in resources: " + inputFile);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            br.readLine(); // skip header (record #1)
            String record;
            recordNumber ++;

            while ((record = br.readLine()) != null) {
                try {
                    String[] fields = record.split(",");
                    Tap currentTap = new Tap();
                    currentTap.setId(Integer.parseInt(fields[0].trim()));
                    currentTap.setDateTimeUTC(LocalDateTime.parse(fields[1].trim(), Constants.DATE_TIME_FORMATTER));
                    currentTap.setTapType(fields[2].trim());
                    currentTap.setStopId(fields[3].trim());
                    currentTap.setCompanyId(fields[4].trim());
                    currentTap.setBusId(fields[5].trim());
                    currentTap.setPan(fields[6].trim());
                    taps.add(currentTap);
                }
                catch (Exception ex) {
                    System.out.println("Skipping record. Issue reading and parsing the record: " + recordNumber);
                }
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }

        return taps;
    }
}
