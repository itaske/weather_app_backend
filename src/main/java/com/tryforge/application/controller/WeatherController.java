package com.tryforge.application.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {


    public String access_key ="28794601dc38ace9cd056e94eb454a38";

    @GetMapping("/")
    public ResponseEntity<?> getWeather(@RequestParam("city") String city, @RequestParam("unit") String unit){
        String defaultUnit = "f";
        if (unit.equals("") || unit == null){
            unit = defaultUnit;
        }
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("access_key", access_key);
        map.put("query", city);
        map.put("unit", unit);
        System.out.println(map);


        ResponseEntity<?> responseEntity = restTemplate.
                getForEntity("http://api.weatherstack.com/current?access_key="+access_key+"&units="+unit+"&query="+city,
                        String.class);
        System.out.println(responseEntity.getBody());

        return responseEntity;
    }
}
