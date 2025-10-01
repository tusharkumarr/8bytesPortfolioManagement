package com.portfolio.management.service;

import com.portfolio.management.model.Asset;
import java.util.List;

public interface AssetService {
    Asset addAssetToPortfolio(Long portfolioId, Asset asset);
    void removeAssetFromPortfolioById(Long assetId);
    List<Asset> getAssetsByPortfolio(Long portfolioId);
    List<Double> getHistoricalPrices(String ticker);
}
