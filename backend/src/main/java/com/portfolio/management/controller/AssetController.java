package com.portfolio.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.portfolio.management.model.Asset;
import com.portfolio.management.model.GenericResponse;
import com.portfolio.management.model.Portfolio;
import com.portfolio.management.model.User;
import com.portfolio.management.service.AssetService;
import com.portfolio.management.repository.PortfolioRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/portfolios/{portfolioId}/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @PostMapping
    public GenericResponse<Asset> addAsset(@PathVariable Long portfolioId,
                                           @RequestBody Asset asset,
                                           HttpServletRequest request) {

        User currentUser = (User) request.getAttribute("currentUser");

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            return GenericResponse.failure("You are not allowed to modify this portfolio");
        }

        Asset savedAsset = assetService.addAssetToPortfolio(portfolioId, asset);
        return GenericResponse.success("Asset added successfully", savedAsset);
    }

    @DeleteMapping("/{assetId}")
    public GenericResponse<?> removeAsset(@PathVariable Long portfolioId,
                                          @PathVariable Long assetId,
                                          HttpServletRequest request) {

        User currentUser = (User) request.getAttribute("currentUser");
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            return GenericResponse.failure("You are not allowed to modify this portfolio");
        }

        assetService.removeAssetFromPortfolioById(assetId);
        return GenericResponse.success("Asset removed successfully", null);
    }

    @GetMapping
    public GenericResponse<List<Asset>> getAssets(@PathVariable Long portfolioId,
                                                  HttpServletRequest request) {

        User currentUser = (User) request.getAttribute("currentUser");
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            return GenericResponse.failure("You are not allowed to view this portfolio");
        }

        List<Asset> assets = assetService.getAssetsByPortfolio(portfolioId);
        return GenericResponse.success("Assets fetched successfully", assets);
    }
    
    @GetMapping("/historical/{ticker}")
    public GenericResponse<List<Double>> getHistoricalPrices(@PathVariable String ticker) {
        List<Double> prices = assetService.getHistoricalPrices(ticker);
        return GenericResponse.success("Historical prices fetched successfully", prices);
    }
}
