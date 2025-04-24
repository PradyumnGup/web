
import React from 'react';
// src/hooks/useRateLimitedFetch.js
import { useEffect, useState, useRef } from 'react';
import { PieChart, Pie, Cell, Legend, Tooltip, ResponsiveContainer } from 'recharts';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid } from 'recharts';

const useRateLimitedFetch = (url, interval = 120000, limit = 20) => {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const timestamps = useRef([]);

  const fetchData = async () => {
    const now = Date.now();
    timestamps.current = timestamps.current.filter(ts => now - ts < interval);
    if (timestamps.current.length >= limit) return;

    setLoading(true);
    try {
      const res = await fetch(url);
      if (!res.ok) throw new Error('API Error');
      const json = await res.json();
      setData(json);
      timestamps.current.push(now);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [url]);

  return { data, error, loading, refetch: fetchData };
};

// export default useRateLimitedFetch;

// src/components/Card.js
const Card = ({ title, value }) => (
  <div className="bg-white dark:bg-gray-800 shadow rounded-xl p-4 w-full sm:w-1/2 md:w-1/3">
    <h3 className="text-xl font-semibold text-gray-800 dark:text-white">{title}</h3>
    <p className="text-2xl text-blue-600 dark:text-blue-400">{value}</p>
  </div>
);
// export default Card;

// src/components/ToggleSwitch.js
const ToggleSwitch = ({ label, isOn, onToggle }) => (
  <div className="flex items-center gap-2">
    <label className="text-gray-700 dark:text-white">{label}</label>
    <button
      className={`w-10 h-5 rounded-full p-1 ${isOn ? 'bg-green-500' : 'bg-gray-400'}`}
      onClick={onToggle}
    >
      <div className={`bg-white w-4 h-4 rounded-full shadow transform transition ${isOn ? 'translate-x-5' : ''}`} />
    </button>
  </div>
);
// export default ToggleSwitch;

// src/components/PieChartComponent.js


const COLORS = ['#0088FE', '#00C49F', '#FFBB28'];

const PieChartComponent = ({ title, data }) => (
  <div className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow">
    <h2 className="text-lg font-semibold text-gray-800 dark:text-white mb-2">{title}</h2>
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          data={data}
          dataKey="value"
          nameKey="name"
          cx="50%"
          cy="50%"
          outerRadius={100}
          label
        >
          {data.map((_, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip />
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  </div>
);

// export default PieChartComponent;

// src/components/AreaChartComponent.js
// 

const AreaChartComponent = ({ data }) => (
  <div className="bg-white dark:bg-gray-800 p-4 rounded-xl shadow">
    <h2 className="text-lg font-semibold text-gray-800 dark:text-white mb-2">Deposits and Bonuses Comparison</h2>
    <ResponsiveContainer width="100%" height={300}>
      <AreaChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="name" />
        <YAxis />
        <Tooltip />
        <Area type="monotone" dataKey="value" stroke="#8884d8" fill="#8884d8" />
      </AreaChart>
    </ResponsiveContainer>
  </div>
);

// export default AreaChartComponent;

// src/pages/Dashboard.js
// import React, { useState } from 'react';
// import useRateLimitedFetch from '../hooks/useRateLimitedFetch';
// import Card from '../components/Card';
// import ToggleSwitch from '../components/ToggleSwitch';
// import PieChartComponent from '../components/PieChartComponent';
// import AreaChartComponent from '../components/AreaChartComponent';

const Dashboard = () => {
  const { data, error, loading } = useRateLimitedFetch('https://d29l1nxcqevrmo.cloudfront.net/dashboard');
  const [analyticsMode, setAnalyticsMode] = useState(false);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error loading data</p>;
  if (!data) return null;

  return (
    <div className="p-4 dark:bg-gray-900 min-h-screen">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Dashboard</h1>
        <ToggleSwitch label="Analytics Mode" isOn={analyticsMode} onToggle={() => setAnalyticsMode(!analyticsMode)} />
      </div>

      {analyticsMode ? (
        <div className="grid gap-6">
          <PieChartComponent title="New Users Distribution" data={[
            { name: 'Today', value: data.todaysUserAddition },
            { name: 'Last 7 Days', value: data.lastSevenDaysUserAddition },
            { name: 'Last 30 Days', value: data.lastThirtyDaysUsersAddition },
          ]} />

          <PieChartComponent title="Active Users Distribution" data={[
            { name: 'Daily', value: data.dailyActiveUsers },
            { name: 'Weekly', value: data.weeklyActiveUsers },
            { name: 'Monthly', value: data.monthlyActiveUsers },
          ]} />

          <AreaChartComponent data={[
            { name: 'Total Deposit', value: data.totalDepositAmount },
            { name: 'Admin Deposit', value: data.totalAdminDepositAmount },
            { name: 'Admin Bonus', value: data.totalAdminBonusAmount },
          ]} />
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          {Object.entries(data).map(([key, value]) => (
            <Card key={key} title={key} value={value} />
          ))}
        </div>
      )}
    </div>
  );
};

// export default Dashboard;
const DarkModeToggle = ({ isDark, onToggle }) => (
  <div className="flex items-center gap-2">
    <span className="text-gray-700 dark:text-white">Dark Mode</span>
    <button
      className={`w-10 h-5 rounded-full p-1 ${isDark ? 'bg-indigo-600' : 'bg-gray-400'}`}
      onClick={onToggle}
    >
      <div
        className={`bg-white w-4 h-4 rounded-full shadow transform transition ${isDark ? 'translate-x-5' : ''}`}
      />
    </button>
  </div>
);

const ChartSwitcher = ({ isAnalytics, onSwitch }) => (
  <div className="flex items-center gap-2">
    <span className="text-gray-700 dark:text-white">Analytics Mode</span>
    <button
      className={`w-10 h-5 rounded-full p-1 ${isAnalytics ? 'bg-green-500' : 'bg-gray-400'}`}
      onClick={onSwitch}
    >
      <div
        className={`bg-white w-4 h-4 rounded-full shadow transform transition ${isAnalytics ? 'translate-x-5' : ''}`}
      />
    </button>
  </div>
);

// src/App.js

// import Dashboard from './pages/Dashboard';

const App = () => {
  return (
    <div className="bg-gray-100 dark:bg-gray-900 min-h-screen">
      <Dashboard />
    </div>
  );
};

export default App ;

