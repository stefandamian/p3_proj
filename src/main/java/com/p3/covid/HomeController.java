package com.p3.covid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index(final Model model){
        StatsEntry var = CovidUtils.getcountryStats("all");
        Integer totalCases = var.getTotalCases();
        Integer newCases = var.getNewCases();
        Integer criticalCases = var.getCriticalCases();
        Integer recoveredCases = var.getRecoveredCases();
        Integer activeCases = var.getActiveCases();
        Integer totalDeaths = var.getTotalDeaths();
        Integer newDeaths = var.getNewDeaths();
        
        ArrayList<StatsEntry> arr = CovidUtils.getcountryStatsTimeline("all", LocalDate.now().plusDays(-45).toString(), LocalDate.now().toString());
        ArrayList<String> days = new ArrayList<String>();
        ArrayList<Integer> arrNewCases = new ArrayList<Integer>();
        ArrayList<Integer> arrNewDeaths = new ArrayList<Integer>();
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyyMMdd");
        for (StatsEntry entry : arr) {
            if (entry != null){
                days.add(entry.getDate().format(formater));
                arrNewCases.add(entry.getNewCases());
                arrNewDeaths.add(entry.getNewDeaths());
            }
        }
        model.addAttribute("total_cases", totalCases);
        model.addAttribute("new_cases", newCases);
        model.addAttribute("critical_cases", criticalCases);
        model.addAttribute("recovered_cases", recoveredCases);
        model.addAttribute("active_cases", activeCases);
        model.addAttribute("total_deaths", totalDeaths);
        model.addAttribute("new_deaths", newDeaths);
        model.addAttribute("days", days);
        model.addAttribute("arr_new_cases", arrNewCases);
        model.addAttribute("arr_new_deaths", arrNewDeaths);


        return "index";
    }

}
