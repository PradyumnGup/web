import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setLanguage, setCurrency } from '../redux/store';
import { useTranslation } from 'react-i18next';

const LanguageCurrencySwitcher = () => {
  const { i18n } = useTranslation();
  const dispatch = useDispatch();
  const { language, currency } = useSelector((state) => state.settings);

  const languages = {
    en: 'English', es: 'Español', fr: 'Français', de: 'Deutsch',
    zh: '中文', ja: '日本語', ar: 'العربية', ru: 'Русский',
    pt: 'Português', hi: 'हिन्दी', ko: '한국어', it: 'Italiano'
  };

  const currencies = ['USD', 'EUR', 'GBP', 'JPY', 'INR', 'AUD', 'CAD', 'CHF', 'CNY', 'RUB', 'KRW', 'BRL'];

  const handleLanguageChange = (e) => {
    const selectedLang = e.target.value;
    i18n.changeLanguage(selectedLang);
    dispatch(setLanguage(selectedLang));
  };

  const handleCurrencyChange = (e) => {
    dispatch(setCurrency(e.target.value));
  };

 


  useEffect(()=>{
    document.body.dir=i18n.dir();//for change in direction of arabic language rtl
  },[i18n,i18n.language])

  return (
    <div>
      <select value={language} onChange={handleLanguageChange}>
        {Object.entries(languages).map(([code, name]) => (
          <option key={code} value={code}>{name}</option>
        ))}
      </select>

      <select value={currency} onChange={handleCurrencyChange}>
        {currencies.map((cur) => (
          <option key={cur} value={cur}>{cur}</option>
        ))}
      </select>
    </div>
  );
};

export default LanguageCurrencySwitcher;
