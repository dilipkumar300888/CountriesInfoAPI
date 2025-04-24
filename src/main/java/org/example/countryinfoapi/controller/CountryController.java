package org.example.countryinfoapi.controller;

import org.example.countryinfoapi.model.Country;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/search")
    public List<Country> searchByCapital(@RequestParam String capital) {
        List<Country> result = new ArrayList<>();
        for (Country country : countries.values()) {
            if(country.getCapital().equalsIgnoreCase(capital)) {
                result.add(country);
            }
        }
        return result;
    }

    @GetMapping("/count")
    public int getCountryCount() {
        return countries.size();
    }

    @GetMapping("/sorted-by-population")
    public List<Country> sortedByPopulation() {
        List<Country> sorted = new ArrayList<>(countries.values());
        sorted.sort((a,b) -> Long.compare(b.getPopulation(), a.getPopulation()));
        return sorted;
    }

    @PatchMapping("/{code}/capital")
    public String updateCountry(@PathVariable("code") String code, @RequestBody Map<String, String> update) {
        Country country = countries.get(code.toUpperCase());
        if(country == null) {
            return "Country not found";
        }
        String capital = update.get("capital");
        country.setCapital(capital);
        return "Capital updated to " + capital;
    }

    @GetMapping("/population-above")
    public List<Country> getContriesAbovePopulation(@RequestParam long min) {
        List<Country> result = new ArrayList<>();
        for (Country country : countries.values()) {
            if(country.getPopulation() > min) {
                result.add(country);
            }
        }
        return result;
    }

    @GetMapping("/most-populated")
    public Country getMostPopulatedCountry() {
        return countries.values().stream()
                .max(Comparator.comparing(Country::getPopulation))
                .orElse(null);
    }

    @GetMapping("/capitals")
    public List<String> getAllCapitals() {
        List<String> capitals = new ArrayList<>();
        for(Country country : countries.values()) {
            capitals.add(country.getCapital());
        }
        return capitals;
    }

    @GetMapping("/names")
    public List<String> getAllCountryNames() {
        return countries.values().stream()
                .map(Country::getName)
                .sorted()
                .toList();
    }

    @GetMapping("/filter-by-name")
    public List<Country> filterByName(@RequestParam String prefix) {
        return countries.values().stream()
                .filter(c -> c.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                .toList();
    }

    @GetMapping("/total-population")
    public long getTotalPopulation() {
        return countries.values().stream()
                .mapToLong(Country::getPopulation)
                .sum();
    }
}
