package com.portfolio.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.portfolio.management.model.GenericResponse;
import com.portfolio.management.model.Portfolio;
import com.portfolio.management.model.User;
import com.portfolio.management.service.PortfolioService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @PostMapping
    public GenericResponse<Portfolio> createPortfolio(HttpServletRequest request,
                                                      @RequestBody Portfolio portfolio) {
        User currentUser = (User) request.getAttribute("currentUser");
        Portfolio savedPortfolio = portfolioService.createPortfolio(currentUser, portfolio);
        return GenericResponse.success("Portfolio created successfully", savedPortfolio);
    }

    @GetMapping
    public GenericResponse<List<Portfolio>> getPortfoliosByUser(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        List<Portfolio> portfolios = portfolioService.getPortfoliosByUser(currentUser);
        return GenericResponse.success("Portfolios fetched successfully", portfolios);
    }

    @GetMapping("/{portfolioId}")
    public GenericResponse<Portfolio> getPortfolioById(@PathVariable Long portfolioId,
                                                       HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        Portfolio portfolio = portfolioService.getPortfolioById(currentUser, portfolioId);
        return GenericResponse.success("Portfolio fetched successfully", portfolio);
    }

    @DeleteMapping("/{portfolioId}")
    public GenericResponse<Void> deletePortfolio(@PathVariable Long portfolioId,
                                                 HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        portfolioService.deletePortfolio(currentUser, portfolioId);
        return GenericResponse.success("Portfolio deleted successfully", null);
    }
}
