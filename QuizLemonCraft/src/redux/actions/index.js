// src/redux/actions/index.js
import {
    SELECT_TAG,
    SET_QUESTIONS,
    NEXT_QUESTION,
    SUBMIT_ANSWER,
    CALCULATE_SCORE,
    SET_TAGS
  } from './types';
  
  // Action to select a tag
  export const selectTag = (tag) => ({
    type: SELECT_TAG,
    payload: tag,
  });
  
  // Action to set questions after matching with tags
  export const setQuestions = (questions) => ({
    type: SET_QUESTIONS,
    payload: questions,
  });
  
  // Action to move to the next question
  export const nextQuestion = () => ({
    type: NEXT_QUESTION,
  });
  
  // Action to submit the user's answers
  export const submitAnswer = (selectedAnswers) => ({
    type: SUBMIT_ANSWER,
    payload: selectedAnswers,
  });
  
  // Action to calculate the final score
  export const calculateScore = () => ({
    type: CALCULATE_SCORE,
  });
  
  export const setTags = (tags) => ({
    type: SET_TAGS,
    payload: tags,
  });