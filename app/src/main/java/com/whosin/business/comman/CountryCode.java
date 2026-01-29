package com.whosin.business.comman;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryCode {
    // Map to hold country name variations and their simplified names
    private static final Map<String, String> countryAliases = new HashMap<>();

    // Static block to initialize the country aliases
    static {
        countryAliases.put("bolivia, plurinational state of", "bolivia");
        countryAliases.put("united states of america", "united states");
        countryAliases.put("usa", "united states");
        countryAliases.put("korea, republic of", "south korea");
        countryAliases.put("south korea", "south korea");
        countryAliases.put("korea, democratic people's republic of", "north korea");
        countryAliases.put("north korea", "north korea");
        countryAliases.put("venezuela, bolivarian republic of", "venezuela");
        countryAliases.put("tanzania, united republic of", "tanzania");
        countryAliases.put("united kingdom of great britain and northern ireland", "united kingdom");
        countryAliases.put("uk", "united kingdom");
        countryAliases.put("england", "united kingdom");
        countryAliases.put("russian federation", "russia");
        countryAliases.put("russia", "russia");
        countryAliases.put("iran, islamic republic of", "iran");
        countryAliases.put("syrian arab republic", "syria");
        countryAliases.put("moldova, republic of", "moldova");
        countryAliases.put("lao people's democratic republic", "laos");
        countryAliases.put("laos", "laos");
        countryAliases.put("viet nam", "vietnam");
        countryAliases.put("micronesia, federated states of", "micronesia");
        countryAliases.put("libya", "libya");
        countryAliases.put("libyan arab jamahiriya", "libya");
        countryAliases.put("gambia, the", "gambia");
        countryAliases.put("the gambia", "gambia");
        countryAliases.put("myanmar", "myanmar");
        countryAliases.put("burma", "myanmar");
        countryAliases.put("congo, the democratic republic of the", "congo - kinshasa");
        countryAliases.put("congo, democratic republic of the", "congo - kinshasa");
        countryAliases.put("congo, republic of the", "congo - brazzaville");
        countryAliases.put("republic of the congo", "congo - brazzaville");
        countryAliases.put("democratic republic of congo", "congo - kinshasa");
        countryAliases.put("côte d'ivoire", "ivory coast");
        countryAliases.put("ivory coast", "ivory coast");
        countryAliases.put("north macedonia", "macedonia");
        countryAliases.put("palestine, state of", "palestine");
        countryAliases.put("state of palestine", "palestine");
        countryAliases.put("czechia", "czech republic");
        countryAliases.put("czech republic", "czech republic");
        countryAliases.put("eswatini", "swaziland");
        countryAliases.put("swaziland", "swaziland");
    }

    // Method to get country code by name
    public static String getCountryCodeByName(String countryName) {
        if (countryName == null || countryName.isEmpty()) {
            return null;
        }

        // Clean the input and convert it to lowercase
        String cleanedCountryName = countryName.replaceAll("\\(.*?\\)", "").trim().toLowerCase();

        // Check if the cleaned name is in the alias map and replace it with the simplified name
        if (countryAliases.containsKey(cleanedCountryName)) {
            cleanedCountryName = countryAliases.get(cleanedCountryName);
        }

        // Loop through ISO countries to find the matching country code
        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            String displayCountry = locale.getDisplayCountry(Locale.ENGLISH).toLowerCase(); // Force English locale

            // Match country names
            if (displayCountry.equals(cleanedCountryName)) {
                return iso; // Return country code if match is found
            }
        }

        // Return null if no match is found
        return null;
    }
}
