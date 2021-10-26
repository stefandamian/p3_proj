package com.p3.covid;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


class StatsEntry{
    private String country;
    private LocalDate time;
    private Integer values[];

    StatsEntry(Map<String,Object> map){
        if (map!=null)
        {
        map = (Map<String, Object>) ((List<Object>) map.get("response")).get(0);
        System.out.println(map);
        country = map.get("country").toString();
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        time = LocalDate.parse(map.get("day").toString(), formater);
        values = new Integer[7];
        Object newCases = ((Map<String, Object>) map.get("cases")).get("new");
        if (newCases==null){
            values[0]=0;
        }
        else{
            values[0] = Integer.parseInt(newCases.toString());
        }
        values[1] = Integer.parseInt(((Map<String, Object>) map.get("cases")).get("active").toString());
        values[2] = Integer.parseInt(((Map<String, Object>) map.get("cases")).get("critical").toString());
        values[3] = Integer.parseInt(((Map<String, Object>) map.get("cases")).get("recovered").toString());
        values[4] = Integer.parseInt(((Map<String, Object>) map.get("cases")).get("total").toString());
        Object newDeaths = ((Map<String, Object>) map.get("deaths")).get("new");
        if (newDeaths==null){
            values[5]=0;
        }
        else{
            values[5] = Integer.parseInt(newDeaths.toString());
        }
        values[6] = Integer.parseInt(((Map<String, Object>) map.get("deaths")).get("total").toString());
        }
    }

    public String getCountry(){
        return country;
    }
    public LocalDate getDate(){
        return time;
    }
    public Integer getNewCases(){
        return values[0];
    }
    public Integer getActiveCases(){
        return values[1];
    }
    public Integer getCriticalCases(){
        return values[2];
    }
    public Integer getRecoveredCases(){
        return values[3];
    }
    public Integer getTotalCases(){
        return values[4];
    }
    public Integer getNewDeaths(){
        return values[5];
    }
    public Integer getTotalDeaths(){
        return values[6];
    }
}

public class CovidUtils {
    static List<String> getCountries(){
        List<String> countriesList = null;
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://covid-193.p.rapidapi.com/countries"))
            .header("x-rapidapi-host", "covid-193.p.rapidapi.com")
            .header("x-rapidapi-key", "c028881ebemshbb6b719df87987bp1dc980jsna096a9b38718")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());    
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (response != null){
            Map<String,Object> map = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(response.body(), Map.class);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            countriesList = (List<String>) map.get("response");
        }
        return countriesList;
    }
    static StatsEntry getcountryStats(String aName){
        return getcountryStats(aName, null);
    };

    static StatsEntry getcountryStats(String aName, String aDate){
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(aDate != null && LocalDate.parse(aDate, formater).isEqual(LocalDate.now())){
            aDate = null;
        }
        String URL = "https://covid-193.p.rapidapi.com/";
        if (aDate != null){
            URL = URL + String.format("history?country=%s&day=%s", aName, aDate);
        }
        else{
            URL = URL + String.format("statistics?country=%s", aName);
        }
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(URL))
            .header("x-rapidapi-host", "covid-193.p.rapidapi.com")
            .header("x-rapidapi-key", "c028881ebemshbb6b719df87987bp1dc980jsna096a9b38718")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());    
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (response != null){
            Map<String,Object> map = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(response.body(), Map.class);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            if(map != null)
                return new StatsEntry(map);
        }
        
        return null;
    };

    static ArrayList<StatsEntry> getcountryStatsTimeline(String aCountry, String aStartDate, String aStopDate){
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ArrayList<StatsEntry> arr = new ArrayList<StatsEntry>();
        LocalDate startDate = LocalDate.parse(aStartDate, formater);
        System.out.println(startDate.toString());
        LocalDate stopDate = LocalDate.parse(aStopDate, formater);
        System.out.println(stopDate.toString());
        while(stopDate.isAfter(startDate) || stopDate.isEqual(startDate)){
            arr.add(getcountryStats(aCountry, startDate.toString()));
            startDate = startDate.plusDays(1);
        }
        return arr;
    }

}
