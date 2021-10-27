package com.p3.covid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


class StatsEntry{
    private String country;
    private LocalDate time;
    private Integer values[];

    protected void finalize(){           
        values = null;
        country = null;
        time = null;        
    }

    StatsEntry(JSONObject obj){
        if (obj!=null)
        {
            System.out.println(obj);
            country = obj.getString("country");
            DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            time = LocalDate.parse(obj.getString("day"), formater);
            values = new Integer[7];

            try {
                values[0] = Integer.parseInt((obj.getJSONObject("cases")).getString("new"));
            } catch (JSONException e) {
                values[0] = 0;
            }
            values[1] = (obj.getJSONObject("cases")).getInt("active");
            values[2] = (obj.getJSONObject("cases")).getInt("critical");
            values[3] = (obj.getJSONObject("cases")).getInt("recovered");
            values[4] = (obj.getJSONObject("cases")).getInt("total");
            try {
                values[5] = Integer.parseInt((obj.getJSONObject("deaths")).getString("new"));
            } catch (JSONException e) {
                values[5] = 0;
            }
            values[6] = (obj.getJSONObject("deaths")).getInt("total");
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

public class CovidUtilsClient {
    private OkHttpClient client;
    //private final static Integer SLEEP_TIME = 1100;

    CovidUtilsClient(){
        client = new OkHttpClient();
    }

    public List<String> getCountries() throws Exception{

        List<String> countriesList = new ArrayList<String>();

        Request request = new Request.Builder()
            .url("https://covid-193.p.rapidapi.com/countries")
            .get()
            .addHeader("x-rapidapi-host", "covid-193.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "c028881ebemshbb6b719df87987bp1dc980jsna096a9b38718")
            .build();
        Response response = null;
        while(!ThreadWithGlobals.canRequest()){
            Thread.sleep(100);
        }
        response = client.newCall(request).execute();
        ThreadWithGlobals.incrementUsedRequests();
        if (response.code() == 200){
            JSONObject obj = new JSONObject(response.body().string());
            if(obj.getInt("results") > 0){
                JSONArray arr = obj.getJSONArray("response");
                for(int i=0;i<arr.length();i++){
                    countriesList.add(arr.getString(i));
                }
            }
        }
        else if(response.code()==429){
            ThreadWithGlobals.couldNotRequest();
            Thread.sleep(1000);
            return getCountries();
        }
        else{
            String message = "Error code: " + (response.code());
            throw new Exception(message);
        }
        if (countriesList.isEmpty())
            return null;
        return countriesList;
    }
    public StatsEntry getcountryStats(String aName) throws Exception{
        return getcountryStats(aName, null);
    };

    public StatsEntry getcountryStats(String aName, String aDate) throws Exception{
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
        Request request = new Request.Builder()
            .url(URL)
            .get()
            .addHeader("x-rapidapi-host", "covid-193.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "c028881ebemshbb6b719df87987bp1dc980jsna096a9b38718")
            .build();
        while(!ThreadWithGlobals.canRequest()){
            Thread.sleep(100);
        }
        Response response = client.newCall(request).execute();
        ThreadWithGlobals.incrementUsedRequests();
        if (response.code() == 200){
            JSONObject obj = new JSONObject(response.body().string());
            if(obj.getInt("results") > 0){
                obj = obj.getJSONArray("response").getJSONObject(0);
                StatsEntry entry = new StatsEntry(obj);
                return entry;
            }
        }
        else if(response.code()==429){
            ThreadWithGlobals.couldNotRequest();
            Thread.sleep(1000);
            return getcountryStats(aName,aDate);
        }
        else{
            String message = "Error code: " + (response.code());
            throw new Exception(message);
        }
        return null;
    };

    public ArrayList<StatsEntry> getcountryStatsTimeline(String aCountry, String aStartDate, String aStopDate) throws Exception{
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ArrayList<StatsEntry> arr = new ArrayList<StatsEntry>();
        LocalDate startDate = LocalDate.parse(aStartDate, formater);
        System.out.println(startDate.toString());
        LocalDate stopDate = LocalDate.parse(aStopDate, formater);
        System.out.println(stopDate.toString());
        while(stopDate.isAfter(startDate) || stopDate.isEqual(startDate)){
            StatsEntry entry = getcountryStats(aCountry, startDate.toString());
            arr.add(entry);
            startDate = startDate.plusDays(1);
        }
        return arr;
    }

}
