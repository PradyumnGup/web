import ProgressBar from "./components/ProgressBar";
import { useState, useEffect } from "react";

export default function App() {
    const [value,setValue]= useState(0);
    const [success,setSuccess] = useState(false);
    
      useEffect(()=>{
        const interval = setInterval(() => {
          setValue((prevValue) => prevValue + 1);
        }, 100);
    
        // Clear the interval when the component unmounts
        return () => clearInterval(interval);
      },[])
    
  return (
      <div className = "App">
          <span> Progress Bar</span>
          <ProgressBar value = {value} onComplete = { () => setSuccess(true) }/>
          <span >{success ? "Complete!" : "Loading..." }</span>
      </div>
  );
}
