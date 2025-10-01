package com.portfolio.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.portfolio.management.model.Asset;
import com.portfolio.management.model.Portfolio;
import com.portfolio.management.repository.AssetRepository;
import com.portfolio.management.repository.PortfolioRepository;
import com.portfolio.management.service.AssetService;
import com.portfolio.management.service.StockService;
import java.io.IOException;
import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockService stockService;

    @Override
    public Asset addAssetToPortfolio(Long portfolioId, Asset asset) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        asset.setPortfolio(portfolio);

        try {
            asset.setCurrentPrice(stockService.getCurrentPrice(asset.getTicker()).doubleValue());
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch stock price for: " + asset.getTicker());
        }

        return assetRepository.save(asset);
    }

    @Override
    public void removeAssetFromPortfolioById(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));
        assetRepository.delete(asset);
    }

    @Override
    public List<Asset> getAssetsByPortfolio(Long portfolioId) {
        List<Asset> assets = assetRepository.findByPortfolioId(portfolioId);

        for (Asset asset : assets) {
            try {
                asset.setCurrentPrice(stockService.getCurrentPrice(asset.getTicker()).doubleValue());
            } catch (IOException e) {
                asset.setCurrentPrice(null);
            }
        }

        return assets;
    }

    @Override
    public List<Double> getHistoricalPrices(String ticker) {
        try {
            return stockService.getHistoricalPrices(ticker);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch historical prices for: " + e);
        }
    }
}
