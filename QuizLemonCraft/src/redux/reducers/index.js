import { combineReducers } from 'redux';
import tagsReducer from './tagsReducer';
import questionsReducer from './questionsReducer';

const rootReducer = combineReducers({
  tags: tagsReducer,
  questions: questionsReducer
});

export default rootReducer;
