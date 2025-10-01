package com.portfolio.management.service.impl;

import com.portfolio.management.service.StockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Value("${alphavantage.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://www.alphavantage.co/query";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Double getCurrentPrice(String ticker) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("function", "TIME_SERIES_INTRADAY")
                    .queryParam("symbol", ticker)
                    .queryParam("interval", "5min")
                    .queryParam("apikey", apiKey)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            if (json.has("Information")) {
                throw new RateLimitExceededException("API rate limit reached. Try again tomorrow.");
            }

            if (json.has("Error Message")) {
                throw new RuntimeException("Alpha Vantage error: " + json.getString("Error Message"));
            }

            if (!json.has("Time Series (5min)")) {
                throw new RuntimeException("No intraday data found for ticker: " + ticker);
            }

            JSONObject timeSeries = json.getJSONObject("Time Series (5min)");
            String latestTime = timeSeries.keys().next();
            return timeSeries.getJSONObject(latestTime).getDouble("1. open");

        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse Alpha Vantage response for: " + ticker + " " + e.getMessage(), e);
        }
    }

    @Override
    public List<Double> getHistoricalPrices(String ticker) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("function", "TIME_SERIES_DAILY")
                    .queryParam("symbol", ticker)
                    .queryParam("apikey", apiKey)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            if (json.has("Information")) {
                throw new RateLimitExceededException("API rate limit reached. Try again tomorrow.");
            }

            if (json.has("Error Message")) {
                throw new RuntimeException("Alpha Vantage error: " + json.getString("Error Message"));
            }

            if (!json.has("Time Series (Daily)")) {
                throw new RuntimeException("No daily historical data found for ticker: " + ticker);
            }

            JSONObject timeSeries = json.getJSONObject("Time Series (Daily)");
            List<Double> historicalPrices = new ArrayList<>();
            timeSeries.keys().forEachRemaining(date -> {
                try {
                    historicalPrices.add(timeSeries.getJSONObject(date).getDouble("4. close"));
                } catch (JSONException ignored) {}
            });

            Collections.reverse(historicalPrices);
            return historicalPrices;

        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse Alpha Vantage historical data for: " + ticker + " " + e.getMessage(), e);
        }
    }

    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }
}
