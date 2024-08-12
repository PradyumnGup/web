// src/redux/reducers/questionsReducer.js
import {
    SET_QUESTIONS,
    NEXT_QUESTION,
    SUBMIT_ANSWER,
    CALCULATE_SCORE,
  } from '../actions/types';
  
  const initialState = {
    questions: [], // The 10 selected questions
    currentQuestionIndex: 0,
    userAnswers: [],
    score: 0,
  };
  
  const calculateQuestionScore = (question, selectedAnswers) => {
    let score = 0;
    if (question.type === 'single') {
      score = question.correct.includes(selectedAnswers[0]) ? 4 : -2;
    } else if (question.type === 'multiple') {
      score = selectedAnswers.reduce((acc, answer) => {
        if (question.correct.includes(answer)) {
          return acc + 1;
        } else {
          return acc - 1;
        }
      }, 0);
  
      // Adjust for all correct answers
      if (selectedAnswers.length === question.correct.length && 
          selectedAnswers.every(answer => question.correct.includes(answer))) {
        score = 4;
      }
    }
    return score;
  };
  
  const questionsReducer = (state = initialState, action) => {
    switch (action.type) {
      case SET_QUESTIONS:
        return {
          ...state,
          questions: action.payload,
        };
      case NEXT_QUESTION:
        return {
          ...state,
          currentQuestionIndex: state.currentQuestionIndex + 1,
        };
      case SUBMIT_ANSWER:
        const currentQuestion = state.questions[state.currentQuestionIndex];
        const questionScore = calculateQuestionScore(currentQuestion, action.payload);
  
        return {
          ...state,
          userAnswers: [...state.userAnswers, action.payload],
          score: state.score + questionScore,
        };
      case CALCULATE_SCORE:
        return {
          ...state,
          score: state.userAnswers.reduce((acc, answer, index) => {
            const questionScore = calculateQuestionScore(state.questions[index], answer);
            return acc + questionScore;
          }, 0),
        };
      default:
        return state;
    }
  };
  
  export default questionsReducer;
  