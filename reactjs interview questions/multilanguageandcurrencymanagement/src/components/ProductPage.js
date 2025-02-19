import React, { useMemo, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useTranslation } from 'react-i18next';
import {  toggleTheme } from '../redux/store';

const products = [
  { id: 1, nameKey: 'Product Name', descriptionKey: 'Product Description', price: 1000, image: 'laptop.jpg' },
  { id: 2, nameKey: 'Product Name', descriptionKey: 'Product Description', price: 500, image: 'phone.jpg' },
];

const currencySymbols = {
  USD: '$', EUR: '€', GBP: '£', JPY: '¥', INR: '₹',
  AUD: 'A$', CAD: 'C$', CHF: 'CHF', CNY: '¥', RUB: '₽',
  KRW: '₩', BRL: 'R$'
};

const exchangeRates = { USD: 1, EUR: 0.85, GBP: 0.75, JPY: 110, INR: 75 }; // Mock rates

const ProductPage = () => {
  const { t } = useTranslation();
  const { language, currency, theme } = useSelector((state) => state.settings);
  const dispatch = useDispatch();
  const [animate, setAnimate] = useState(false);
  const [initialLoad, setInitialLoad] = useState(true);  // To track the first load
  
  useEffect(() => {
    if (initialLoad) {
      setAnimate(true);
      const timeout = setTimeout(() => setAnimate(false), 500);  // Timeout for animation duration
      setInitialLoad(false);  // After first load, don't animate on state change
      return () => clearTimeout(timeout);
    }
  }, [initialLoad]); // Runs only once when the component is first loaded
  
  // Format currency according to locale
  const formatCurrency = useMemo(() => {
    return (price) => {
      const locale = language === 'en' ? 'en-US' : 'es-ES';
      return new Intl.NumberFormat(locale, {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }).format(price);
    };
  }, [language, currency]);

  const formattedDateTime = useMemo(() => {
    return new Intl.DateTimeFormat(language, {
      day: 'numeric',     // Day of the month (e.g., 18)
      month: 'long',      // Full month name (e.g., February)
      year: 'numeric',    // Full year (e.g., 2025)
    }).format(new Date());
  }, [language]);
  
  

  const handleThemeToggle = () => {
    dispatch(toggleTheme());
  };

  return (
    <div className={`slide-transition ${animate ? 'slide-in' : ''}`}>
      <h1>{t('Products')}</h1>
      <button onClick={handleThemeToggle} aria-label="Toggle theme">
        {theme === 'light' ? 'Switch to Dark Mode' : 'Switch to Light Mode'}
      </button>
      <div className="product-list" role="list">
        {products.map((product) => (
          <div 
            key={product.id} 
            className="product-card" 
            role="listitem" 
            tabIndex="0"
            aria-labelledby={`product-name-${product.id}`}
            aria-describedby={`product-description-${product.id}`}
            aria-live="polite"
          >
            <h3 id={`product-name-${product.id}`} role="heading" aria-level="3">
              {t(`${product.nameKey}`)}
            </h3>
            <p id={`product-description-${product.id}`} aria-live="polite">
              {t(`${product.descriptionKey}`)}
            </p>
            <p className="price" aria-live="polite">
              {formatCurrency(product.price)}
            </p>
            <img 
              src={product.image} 
              alt={t(`${product.nameKey}`)} 
              width="100" 
              aria-hidden="true"
            />
          </div>
        ))}
      </div>
      
      {/* Content Management Section */}
      <div className="content-management">
        <section>
          <h2>{t('About Us')}</h2>
          <p>{t('Company History')}</p>
        </section>
        
        <section>
          <h2>{t('Contact Us')}</h2>
          <p>{t('Phone')}: {t('Phone Number')}</p>
          <p>{t('Address')}: {t('Company Address')}</p>
        </section>
        
        <section>
          <h2>{t('Promotions')}</h2>
          <p>{t('Promo Banner')}</p>
        </section>
        
        <section>
          <h2>{t('Date & Time')}</h2>
          <p>{formattedDateTime}</p>
        </section>
      </div>
    </div>
  );
};

export default ProductPage;
