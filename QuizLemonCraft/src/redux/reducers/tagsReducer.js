// src/redux/reducers/tagsReducer.js
import { SELECT_TAG,SET_TAGS } from '../actions/types';

const initialState = {
  availableTags: [], // This should be populated with the 100 tags you provide
  selectedTags: [],
};

const tagsReducer = (state = initialState, action) => {
  switch (action.type) {
    case SELECT_TAG:
      if (state.selectedTags.length >= 20) return state;
      if (state.selectedTags.includes(action.payload)) {
        return {
          ...state,
          selectedTags: state.selectedTags.filter(tag => tag !== action.payload),
        };
      } else {
        return {
          ...state,
          selectedTags: [...state.selectedTags, action.payload],
        };
      }
      case SET_TAGS:
      return {
        ...state,
        availableTags: action.payload,
        
      };
    default:
      return state;
  }
};

export default tagsReducer;
