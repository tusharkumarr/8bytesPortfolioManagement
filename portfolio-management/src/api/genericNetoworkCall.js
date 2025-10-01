import axios from "axios";

const API_BASE = process.env.REACT_APP_API_URL || "http://localhost:8080";

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const data = error.response?.data || { success: false, message: error.message };
    return Promise.reject(data);
  }
);

export default api;
