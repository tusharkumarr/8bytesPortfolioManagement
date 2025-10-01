package com.portfolio.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.management.model.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByPortfolioId(Long portfolioId);
    Optional<Asset> findByPortfolioIdAndTicker(Long portfolioId, String ticker);
}