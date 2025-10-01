package com.portfolio.management.service;

import java.io.IOException;
import java.util.List;

public interface StockService {
    Double getCurrentPrice(String ticker) throws IOException;
    List<Double> getHistoricalPrices(String ticker) throws IOException;
}
