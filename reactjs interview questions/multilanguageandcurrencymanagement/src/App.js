import React,{ useEffect,Suspense, lazy }  from 'react';
import { useSelector } from 'react-redux';
// import LanguageCurrencySwitcher from './components/LanguageCurrencySwitcher';
// import ProductPage from './components/ProductPage';
import './i18n';
import './App.css';

const ProductPage = lazy(() => import('./components/ProductPage'));
const LanguageCurrencySwitcher = lazy(() => import('./components/LanguageCurrencySwitcher'));
const App = () => {
  const theme = useSelector((state) => state.settings.theme);
  // Apply the selected theme to the body
  useEffect(() => {
    document.body.className = theme;
  }, [theme]);

  return (
      <div>
        <Suspense fallback={<div>Loading...</div>}>
        <h2>Multi-Language & Multi-Currency App</h2>
        <LanguageCurrencySwitcher />
        <ProductPage />
        </Suspense>
      </div>
    
  );
};

export default App;
