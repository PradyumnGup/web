import React, { useEffect, useState } from 'react';

function Timer({ onTimeUp }) {
  const [timeRemaining, setTimeRemaining] = useState(30);

  useEffect(() => {
    if (timeRemaining === 0) {
      onTimeUp();
      return;
    }

    const timer = setInterval(() => {
      setTimeRemaining((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [timeRemaining, onTimeUp]);

  return (
    <div>
      Time Remaining: {timeRemaining}s
    </div>
  );
}

export default Timer;
