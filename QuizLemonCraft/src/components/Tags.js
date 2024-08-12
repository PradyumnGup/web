import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { selectTag } from '../redux/actions/index';

function Tags() {
  const tags = useSelector((state) => state.tags.availableTags);
  const dispatch = useDispatch();
  
  const handleTagSelect = (tag) => {
    dispatch(selectTag(tag));
  };

  if (!Array.isArray(tags) || tags.length === 0) {
    return <div>No tags available</div>;
  }
  
  return (
    <div>
      {tags.map((tag) => (
        <button key={tag} onClick={() => handleTagSelect(tag)}>{tag}</button>
      ))}
    </div>
  );
}

export default Tags;
