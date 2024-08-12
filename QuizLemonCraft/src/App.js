import React,{useEffect } from 'react';

import { useDispatch } from 'react-redux';
import { setQuestions,setTags } from './redux/actions/index';

import Tags from './components/Tags';
import Question from './components/Question';

function App() {
  const dispatch = useDispatch();
  useEffect(() => {
    const fetchQuestions = async () => {
      try {
        const response = await fetch('/ques.json'); // Fetching from the public folder
        const data = await response.json();

        //  the JSON structure is something like: { uniqueTags: [...], questions: [...] }
        const tags = data.uniqueTags || [];
        const questions = data.questions || [];

        // Dispatch an action to set tags and questions in the store
        dispatch(setQuestions(questions));

        // If you want to set tags in a separate action
        dispatch(setTags(tags)); // If you have an action to set tags separately
      } catch (error) {
        console.error("Error fetching questions: ", error);
      }
    };

    fetchQuestions();
  }, [dispatch]);
  return (
    
      <div className="App">
        <Tags />
        <Question />
      </div>
    
  );
}

export default App;
