package com.portfolio.management.service;

import com.portfolio.management.model.Portfolio;
import com.portfolio.management.model.User;

import java.util.List;

public interface PortfolioService {
    Portfolio createPortfolio(User user, Portfolio portfolio);
    List<Portfolio> getPortfoliosByUser(User user);
    Portfolio getPortfolioById(User user, Long portfolioId);
    void deletePortfolio(User user, Long portfolioId);
}
