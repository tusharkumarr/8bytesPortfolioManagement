package com.portfolio.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.portfolio.management.model.Portfolio;
import com.portfolio.management.model.User;
import com.portfolio.management.repository.PortfolioRepository;
import com.portfolio.management.service.PortfolioService;

import java.util.List;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Override
    public Portfolio createPortfolio(User user, Portfolio portfolio) {
        portfolio.setUser(user);
        return portfolioRepository.save(portfolio);
    }

    @Override
    public List<Portfolio> getPortfoliosByUser(User user) {
        return portfolioRepository.findByUserId(user.getId());
    }

    @Override
    public Portfolio getPortfolioById(User user, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + portfolioId));

        if (!portfolio.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied for this portfolio");
        }

        return portfolio;
    }

    @Override
    public void deletePortfolio(User user, Long portfolioId) {
        Portfolio portfolio = getPortfolioById(user, portfolioId);
        portfolioRepository.delete(portfolio);
    }
}
