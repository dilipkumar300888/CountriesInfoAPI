package org.example.countryinfoapi.controller;

import org.example.countryinfoapi.model.Country;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {
    private Map<String, Country> countries = new HashMap<>();

    @GetMapping
    public Collection<Country> getCountries() {
        return countries.values();
    }

    @GetMapping("/{code}")
    public Country getCountryByCode(@PathVariable("code") String code) {
        return countries.getOrDefault(code.toUpperCase(),null);
    }

    @PostMapping
    public Country addCountry(@RequestBody Country country) {
        countries.put(country.getCode().toUpperCase(),country);
        return country;
    }

    @DeleteMapping("/{code}")
    public String deleteCountry(@PathVariable("code") String code) {
        return countries.remove(code.toUpperCase()) != null ? "Deleted Successfully!" : "Country not found!";
    }

}
