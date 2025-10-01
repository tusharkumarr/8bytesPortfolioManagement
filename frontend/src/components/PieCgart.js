import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend
} from 'chart.js';
import { Pie } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

export default function PieAllocation({ assets }) {
  const data = {
    labels: assets.map(a => a.ticker),
    datasets: [
      {
        data: assets.map(a => a.quantity * (a.currentPrice || a.averagePrice || 0)),
        backgroundColor: [
          '#1976d2', '#d32f2f', '#fbc02d', '#388e3c', '#7b1fa2', '#f57c00'
        ]
      }
    ]
  };

  return <Pie data={data} />;
}
