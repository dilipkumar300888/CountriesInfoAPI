package org.example.countryinfoapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InfoController {

    @GetMapping("/version")
    public Map<String, String> getApiVersion() {
        Map<String, String> info = new HashMap<>();
        info.put("version", "1.0.0");
        info.put("status","stable");
        info.put("lastUpdated","2025-04-29");
        return info;
    }
}
