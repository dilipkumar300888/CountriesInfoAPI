package org.example.countryinfoapi.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.countryinfoapi.model.Country;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {
    private final View error;
    private Map<String, Country> countries = new HashMap<>();

    public CountryController(View error) {
        this.error = error;
    }

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

    @GetMapping("/top-populated")
    public List<Country> getTopPopulatedCountries(@RequestParam(defaultValue = "3") int count) {
        return countries.values().stream()
                .sorted((a,b) -> Long.compare(b.getPopulation(),a.getPopulation()))
                .limit(count)
                .toList();
    }

    @GetMapping("/min-population")
    public List<Country> getCountriesWithMinPopulation(@RequestParam long min) {
        return countries.values().stream()
                .filter(country -> country.getPopulation() >= min)
                .toList();
    }

    @GetMapping("/population-range")
    public List<Country> getCountriesByPopulationRange(@RequestParam long min,@RequestParam long max) {
        return countries.values().stream()
                .filter(country -> country.getPopulation() >= min && country.getPopulation() <= max)
                .toList();
    }

    @GetMapping("/random")
    public Country getRandomCountry() {
        List<Country> countryList = new ArrayList<>(countries.values());
        if(countryList.isEmpty()) {
            return null;
        }
        Random rand = new Random();
        return countryList.get(rand.nextInt(countryList.size()));
    }

    @GetMapping(value = "/export",produces = "text/cvs")
    public void exportCountriesToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/cvs");
        response.setHeader("Content-Disposition", "attachment; filename=countries.csv");
        PrintWriter writer = response.getWriter();
        writer.println("Code,Name,Capital,Population");

        for(Country country : countries.values()) {
            writer.printf("%s,%s,%s,%d%n",country.getCode(),country.getName(),country.getCapital(),country.getPopulation());
        }
        writer.flush();
        writer.close();
    }

    @GetMapping("/grouperd-by-first-letter")
    public Map<Character,List<Country>> groupCountriesByFirstLetter() {
        return countries.values().stream()
                .collect(Collectors.groupingBy(c -> Character.toUpperCase(c.getName().charAt(0))));
    }

    @GetMapping("/search")
    public List<Country> searchCountries(@RequestParam String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return countries.values().stream()
                .filter(country ->
                        country.getName().toLowerCase().contains(lowerKeyword) ||
                        country.getCapital().toLowerCase().contains(lowerKeyword))
                .toList();
    }

    @GetMapping("/count-by-capital")
    public Map<String, Long> countCountriesByCapital() {
        return countries.values().stream()
                .collect(Collectors.groupingBy(
                        Country::getCapital,
                        Collectors.counting()
                ));
    }

    @PostMapping
    public ResponseEntity<?> createCountry(@Valid @RequestParam Country country, BindingResult result) {
        if(result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(errors);
        }
        String code = country.getCode() != null ? country.getCode() : UUID.randomUUID().toString().substring(0,2).toUpperCase();
        country.setCode(code);
        countries.put(code.hashCode(), country);
        return ResponseEntity.status(HttpStatus.CREATED).body(country);
    }

}
