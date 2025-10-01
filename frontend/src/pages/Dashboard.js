import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  fetchPortfolios as apiFetchPortfolios,
  fetchAssets as apiFetchAssets,
  addAsset as apiAddAsset,
  deleteAsset as apiDeleteAsset,
  fetchHistoricalPrices as apiFetchHistorical,
  createPortfolio as createPortfolio,
  deletePortfolio as apiDeletePortfolio,
} from "../api/api";
import StockChart from "../components/StockChart";
import PieAllocation from "../components/PieCgart";

const ALL_TICKERS = [
  "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "NVDA",
  "FB", "NFLX", "BABA", "INTC", "AMD", "ORCL", "IBM"
];

export default function Dashboard() {
  const navigate = useNavigate();
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uiError, setUiError] = useState("");
  const [adding, setAdding] = useState(false);

  // new states for portfolio creation
  const [newPortfolioName, setNewPortfolioName] = useState("");
  const [creating, setCreating] = useState(false);

  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) navigate("/login");
    loadAll();
  }, []);

  async function loadAll() {
    setLoading(true);
    setUiError("");
    try {
      const pRes = await apiFetchPortfolios();

      if (!pRes.success) {
        setUiError(pRes.message || "Failed to fetch portfolios");
        setPortfolios([]);
        return;
      }

      const fetched = await Promise.all(
        pRes.data.map(async (p) => {
          const aRes = await apiFetchAssets(p.id);
          p.assets = aRes.success && Array.isArray(aRes.data) ? aRes.data : [];
          return p;
        })
      );

      setPortfolios(fetched);
    } catch (err) {
      console.error(err);
      setUiError(err.message || "Server error while loading portfolios");
    } finally {
      setLoading(false);
    }
  }

  async function handleAdd(portfolioId, ticker, quantity, averagePrice, onComplete) {
    setAdding(true);
    try {
      const payload = {
        ticker,
        quantity: parseFloat(quantity),
        averagePrice: averagePrice ? parseFloat(averagePrice) : undefined,
      };
      const res = await apiAddAsset(portfolioId, payload);
      if (!res.success) {
        alert(res.message || "Failed to add asset");
      } else {
        await loadAll();
      }
    } catch (err) {
      alert(err.message || "Error adding asset");
    } finally {
      setAdding(false);
      onComplete && onComplete();
    }
  }

  async function handleDelete(portfolioId, assetId) {
    if (!window.confirm("Delete this asset?")) return;
    try {
      const res = await apiDeleteAsset(portfolioId, assetId);
      if (!res.success) {
        alert(res.message || "Failed to delete asset");
      } else {
        await loadAll();
      }
    } catch (err) {
      alert(err.message || "Failed to delete asset");
    }
  }

  async function showHistorical(portfolioId, ticker, setter) {
    try {
      setter({ loading: true, error: null, data: null });
      const prices = await apiFetchHistorical(portfolioId, ticker);
      setter({ loading: false, error: null, data: prices });
    } catch (err) {
      setter({ loading: false, error: err.message || "No historical data", data: null });
    }
  }

  async function handleCreatePortfolio() {
    if (!newPortfolioName.trim()) {
      alert("Portfolio name is required");
      return;
    }
    setCreating(true);
    try {
      const res = await createPortfolio({ name: newPortfolioName.trim() });
      if (!res.success) {
        alert(res.message || "Failed to create portfolio");
      } else {
        setNewPortfolioName("");
        await loadAll();
      }
    } catch (err) {
      alert(err.message || "Error creating portfolio");
    } finally {
      setCreating(false);
    }
  }

  async function handleDeletePortfolio(portfolioId) {
    if (!window.confirm("Delete this portfolio and all assets?")) return;
    try {
      const res = await apiDeletePortfolio(portfolioId);
      if (!res.success) {
        alert(res.message || "Failed to delete portfolio");
      } else {
        await loadAll();
      }
    } catch (err) {
      alert(err.message || "Error deleting portfolio");
    }
  }

  if (loading) return <div style={{ padding: 40, textAlign: "center" }}>Loading portfolios...</div>;
  if (uiError) return <div style={{ padding: 40, color: "red", textAlign: "center" }}>{uiError}</div>;

  return (
    <div style={{ maxWidth: 1100, margin: "30px auto", fontFamily: "Arial, sans-serif" }}>
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 18 }}>
        <h2 style={{ margin: 0 }}>My Investment Dashboard</h2>
        <div>
          <button onClick={loadAll} style={styles.primary}>Refresh</button>
          <button onClick={() => { localStorage.removeItem("token"); navigate("/login"); }} style={styles.secondary}>Sign out</button>
        </div>
      </header>

      {/* Add Portfolio Section */}
      <div style={{ marginBottom: 24, padding: 20, border: "1px solid #eee", borderRadius: 8 }}>
        <h3>Create New Portfolio</h3>
        <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
          <input
            placeholder="Portfolio name"
            value={newPortfolioName}
            onChange={(e) => setNewPortfolioName(e.target.value)}
            style={styles.smallInput}
          />
          <button onClick={handleCreatePortfolio} style={styles.primary} disabled={creating}>
            {creating ? "Creating..." : "Add Portfolio"}
          </button>
        </div>
      </div>

      {portfolios.length === 0 ? (
        <div style={{ padding: 40, textAlign: "center" }}>No portfolios found.</div>
      ) : (
        portfolios.map((p) => (
          <PortfolioCard
            key={p.id}
            portfolio={p}
            onAdd={handleAdd}
            onDelete={handleDelete}
            onDeletePortfolio={handleDeletePortfolio}
            showHistorical={showHistorical}
            adding={adding}
            availableTickers={ALL_TICKERS}
          />
        ))
      )}
    </div>
  );
}

function PortfolioCard({ portfolio, onAdd, onDelete, onDeletePortfolio, showHistorical, adding, availableTickers = [] }) {
  const [ticker, setTicker] = useState("");
  const [quantity, setQuantity] = useState("");
  const [avgPrice, setAvgPrice] = useState("");
  const [histMap, setHistMap] = useState({});

  const setHist = (tick, v) => setHistMap(prev => ({ ...prev, [tick]: v }));

  return (
    <div style={{ marginBottom: 24, padding: 20, borderRadius: 8, background: "#fff", boxShadow: "0 6px 18px rgba(0,0,0,0.06)" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
        <div>
          <h3 style={{ margin: "0 0 8px 0" }}>{portfolio.name}</h3>
          <div style={{ color: "#666", marginBottom: 12 }}>Owner: {portfolio.user?.phoneNumber || "you"}</div>

          <div style={{ display: "flex", gap: 12, alignItems: "center", marginBottom: 12 }}>
            <select value={ticker} onChange={(e) => setTicker(e.target.value)} style={styles.smallInput}>
              <option value="">Select ticker</option>
              {availableTickers.map(t => <option key={t} value={t}>{t}</option>)}
            </select>

            <input placeholder="Quantity" value={quantity} onChange={(e)=>setQuantity(e.target.value)} style={styles.smallInput}/>
            <input placeholder="Avg price (optional)" value={avgPrice} onChange={(e)=>setAvgPrice(e.target.value)} style={styles.smallInput}/>

            <button onClick={() => {
              if (!ticker || !quantity) { alert("Ticker & quantity required"); return; }
              onAdd(portfolio.id, ticker.trim().toUpperCase(), quantity, avgPrice, () => { setTicker(""); setQuantity(""); setAvgPrice(""); });
            }} style={styles.primary} disabled={adding}>Add</button>
          </div>
        </div>

        <div style={{ textAlign: "right" }}>
          <div style={{ marginBottom: 8, color: "#333", fontWeight: 600 }}>Total value</div>
          <div style={{ fontSize: 18, fontWeight: 700 }}>
            {formatCurrency(totalPortfolioValue(portfolio.assets))}
          </div>
          <button onClick={() => onDeletePortfolio(portfolio.id)} style={styles.linkDanger}>Delete Portfolio</button>
        </div>
      </div>

      <div style={{ display: "flex", gap: 16, marginTop: 14 }}>
        <div style={{ flex: 1 }}>
          {(!portfolio.assets || portfolio.assets.length === 0) ? (
            <div style={{ color: "#666", padding: 20 }}>No assets yet</div>
          ) : (
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr style={{ textAlign: "left", borderBottom: "1px solid #eee" }}>
                  <th style={{ padding: 8 }}>Ticker</th>
                  <th style={{ padding: 8 }}>Qty</th>
                  <th style={{ padding: 8 }}>Current</th>
                  <th style={{ padding: 8 }}>Total</th>
                  <th style={{ padding: 8 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {portfolio.assets.map(a => (
                  <tr key={a.id} style={{ borderBottom: "1px solid #fafafa" }}>
                    <td style={{ padding: 8, fontWeight: 600 }}>{a.ticker}</td>
                    <td style={{ padding: 8 }}>{a.quantity}</td>
                    <td style={{ padding: 8 }}>{a.currentPrice ? a.currentPrice.toFixed(2) : (a.averagePrice ? a.averagePrice.toFixed(2) : "N/A")}</td>
                    <td style={{ padding: 8 }}>{formatCurrency((a.currentPrice || a.averagePrice || 0) * (a.quantity || 0))}</td>
                    <td style={{ padding: 8 }}>
                      <button style={styles.link} onClick={() => showHistorical(portfolio.id, a.ticker, (v)=>setHist(a.ticker, v))}>Chart</button>
                      <button style={styles.linkDanger} onClick={()=>onDelete(portfolio.id, a.id)}>Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <aside style={{ width: 280 }}>
          <div style={{ marginBottom: 12, fontWeight: 700 }}>Allocation</div>
          <PieAllocation assets={portfolio.assets || []} />
        </aside>
      </div>

      {Object.entries(histMap).map(([tick, info]) => (
        <div key={tick} style={{ marginTop: 12 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div style={{ fontWeight: 700 }}>{tick} — Historical</div>
            <div style={{ color: "#666", fontSize: 13 }}>{info?.loading ? "Loading..." : info?.error ? info.error : ""}</div>
          </div>
          {info?.data && <StockChart prices={info.data} />}
        </div>
      ))}
    </div>
  );
}

function totalPortfolioValue(assets = []) {
  return assets.reduce((sum, a) => sum + ((a.currentPrice || a.averagePrice || 0) * (a.quantity || 0)), 0);
}

function formatCurrency(n) {
  if (!n && n !== 0) return "—";
  return "$" + Number(n).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

const styles = {
  primary: { marginLeft: 8, background: "#1976d2", color: "#fff", border: "none", padding: "8px 12px", borderRadius: 6, cursor: "pointer" },
  secondary: { marginLeft: 8, background: "#eee", color: "#333", border: "none", padding: "8px 12px", borderRadius: 6, cursor: "pointer" },
  smallInput: { padding: "8px 10px", borderRadius: 6, border: "1px solid #ddd", width: 130 },
  link: { marginRight: 8, background: "none", border: "none", color: "#1976d2", cursor: "pointer" },
  linkDanger: { marginRight: 8, background: "none", border: "none", color: "#d32f2f", cursor: "pointer" }
};
