package com.p3.covid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index(final Model model) throws Exception{
        CovidUtilsClient client = new CovidUtilsClient();

        List<String> validCountries = client.getCountries();
        model.addAttribute("countries", validCountries);
        //*/

        return "index";
    }

    @GetMapping("/stats")
    public String country_stats(@RequestParam(name="country") String country, @RequestParam(name="startDate", required = false) String startDate, 
    @RequestParam(name="stopDate", required = false) String stopDate, final Model model) throws Exception{
        
        CovidUtilsClient client = new CovidUtilsClient();
        model.addAttribute("country", country);
        if (country.equalsIgnoreCase("all")){
            model.addAttribute("country_name", "World Wide");
        }
        else{
            List<String> valid_countries = client.getCountries();
            if(valid_countries.stream().noneMatch(country::equalsIgnoreCase)){
                return "index";
            }
            model.addAttribute("country_name", country);
        }        
        
        StatsEntry var = client.getCountryStats(country);
        Integer totalCases = var.getTotalCases();
        Integer newCases = var.getNewCases();
        Integer criticalCases = var.getCriticalCases();
        Integer recoveredCases = var.getRecoveredCases();
        Integer activeCases = var.getActiveCases();
        Integer totalDeaths = var.getTotalDeaths();
        Integer newDeaths = var.getNewDeaths();

        model.addAttribute("total_cases", totalCases);
        model.addAttribute("new_cases", newCases);
        model.addAttribute("critical_cases", criticalCases);
        model.addAttribute("recovered_cases", recoveredCases);
        model.addAttribute("active_cases", activeCases);
        model.addAttribute("total_deaths", totalDeaths);
        model.addAttribute("new_deaths", newDeaths);
        
        if (startDate == null){
            startDate = LocalDate.now().plusDays(-30).toString();
        }
        if (stopDate == null){
            stopDate = LocalDate.now().toString();
        }

        ArrayList<StatsEntry> arr = client.getCountryStatsTimeline(country, startDate, stopDate);
        ArrayList<String> days = new ArrayList<>();
        ArrayList<Integer> arrNewCases = new ArrayList<>();
        ArrayList<Integer> arrNewDeaths = new ArrayList<>();
        ArrayList<Integer> arrActiveCases = new ArrayList<>();
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyyMMdd");
        Integer number = 0;
        for (StatsEntry entry : arr) {
            if (entry != null){
                days.add(entry.getDate().format(formater));
                arrNewCases.add(entry.getNewCases());
                arrNewDeaths.add(entry.getNewDeaths());
                arrActiveCases.add(entry.getActiveCases());
                number++;
            }
        }
        System.out.println(arr.size());
        System.out.println(number);
        model.addAttribute("start_date", startDate);
        model.addAttribute("stop_date", stopDate);
        model.addAttribute("days", days);
        model.addAttribute("arr_new_cases", arrNewCases);
        model.addAttribute("arr_new_deaths", arrNewDeaths);
        model.addAttribute("arr_active_cases", arrActiveCases);

        //*/
        return "stats";
    }

}
