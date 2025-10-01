import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/api";

export default function Login() {
  const navigate = useNavigate();
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await login(phoneNumber, password);
      if (res.success) {
        localStorage.setItem("token", res.data.token);
      localStorage.setItem("userId", res.data.userId || "");
        navigate("/dashboard");
      } else {
        setError(res.data?.message || "Login failed");
      }
    } catch (err) {
      console.error("Login error:", err);
      setError(err.response?.data?.message || "Server error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={{ marginBottom: 10 }}>Sign in</h2>
        <form onSubmit={onSubmit}>
          <input
            style={styles.input}
            placeholder="Phone number"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
            required
          />
          <input
            style={styles.input}
            placeholder="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button style={styles.button} disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}
          </button>
          {error && <div style={styles.error}>{error}</div>}
        </form>
      </div>
    </div>
  );
}

const styles = {
  container: { display: "flex", height: "100vh", alignItems: "center", justifyContent: "center", background: "#f4f6f8" },
  card: { width: 360, padding: 24, borderRadius: 8, boxShadow: "0 6px 20px rgba(0,0,0,0.08)", background: "#fff" },
  input: { width: "100%", padding: "10px 12px", marginBottom: 12, fontSize: 14, borderRadius: 6, border: "1px solid #ddd" },
  button: { width: "100%", padding: "10px", background: "#1976d2", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" },
  error: { marginTop: 12, color: "#d32f2f" }
};
