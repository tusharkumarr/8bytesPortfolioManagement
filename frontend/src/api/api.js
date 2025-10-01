import api from "./genericNetoworkCall";

export const login = async (phoneNumber, password) => {
  const res = await api.post("/auth/login", { phoneNumber, password });
  return res.data;
};

export const fetchPortfolios = async () => {
  const res = await api.get("/portfolios");
  return res.data;
};

export const createPortfolio = async (portfolio) => {
  const res = await api.post("/portfolios", portfolio);
  return res.data;
};
export const deletePortfolio = async (portfolioId) => {
  const res = await api.delete(`/portfolios/${portfolioId}`);
  return res.data;
};


export const fetchAssets = async (portfolioId) => {
  const res = await api.get(`/portfolios/${portfolioId}/assets`);
  return res.data;
};

export const addAsset = async (portfolioId, asset) => {
  const res = await api.post(`/portfolios/${portfolioId}/assets`, asset);
  return res.data;
};

export const deleteAsset = async (portfolioId, assetId) => {
  const res = await api.delete(`/portfolios/${portfolioId}/assets/${assetId}`);
  return res.data;
};

export const fetchHistoricalPrices = async (portfolioId, ticker) => {
  const res = await api.get(
    `/portfolios/${portfolioId}/assets/historical/${encodeURIComponent(ticker)}`
  );
  return res.data.data;
};

export const fetchCurrentPrice = async (ticker) => {
  const res = await api.get(`/stocks/${encodeURIComponent(ticker)}/price`);
  return res.data;
};
