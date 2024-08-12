import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { nextQuestion, submitAnswer } from '../redux/actions';

function Question() {
  const dispatch = useDispatch();
  const currentQuestion = useSelector((state) => state.questions[state.currentQuestionIndex]);
  const [selectedAnswers, setSelectedAnswers] = useState([]);
  const [timeRemaining, setTimeRemaining] = useState(30);

  useEffect(() => {
    if (timeRemaining === 0) {
      handleNextQuestion();
    }
    
    const timer = setInterval(() => {
      setTimeRemaining((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [timeRemaining]);

  const handleAnswerChange = (answer) => {
    setSelectedAnswers((prev) => 
      prev.includes(answer) 
        ? prev.filter((item) => item !== answer)
        : [...prev, answer]
    );
  };

  const handleSubmit = () => {
    dispatch(submitAnswer(selectedAnswers));
    handleNextQuestion();
  };

  const handleNextQuestion = () => {
    dispatch(nextQuestion());
    setSelectedAnswers([]);
    setTimeRemaining(30);
  };

  if (!currentQuestion) return <div>Loading...</div>;

  return (
    <div>
      <h2>{currentQuestion.question}</h2>
      {currentQuestion.answers.map((answer) => (
        <div key={answer}>
          <input
            type={currentQuestion.type === 'multiple' ? 'checkbox' : 'radio'}
            name="answer"
            value={answer}
            checked={selectedAnswers.includes(answer)}
            onChange={() => handleAnswerChange(answer)}
          />
          {answer}
        </div>
      ))}
      <button onClick={handleSubmit}>Submit Answer</button>
      <div>Time Remaining: {timeRemaining}s</div>
    </div>
  );
}

export default Question;
