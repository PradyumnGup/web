import { configureStore, createSlice } from '@reduxjs/toolkit';

// Language & Currency Slice
const settingsSlice = createSlice({
  name: 'settings',
  initialState: {
    language: localStorage.getItem('language') || 'en',
    currency: localStorage.getItem('currency') || 'USD',
    theme: localStorage.getItem('theme') || 'light',
  },
  reducers: {
    setLanguage: (state, action) => {
      state.language = action.payload;
      localStorage.setItem('language', action.payload);
    },
    setCurrency: (state, action) => {
      state.currency = action.payload;
      localStorage.setItem('currency', action.payload);
    },
    toggleTheme: (state) => {
      const newTheme = state.theme === 'light' ? 'dark' : 'light';
      state.theme = newTheme;
      localStorage.setItem('theme', newTheme); // Persist theme in localStorage
    },
  },
});

export const { setLanguage, setCurrency,toggleTheme } = settingsSlice.actions;

const store = configureStore({
  reducer: {
    settings: settingsSlice.reducer,
  },
});

export default store;
