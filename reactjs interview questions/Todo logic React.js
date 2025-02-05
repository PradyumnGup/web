import { useState } from 'react';

import './app.css';

function App() {
  const [clicked, setClicked] = useState(false);
  const [count, setCount] = useState(0);
  const [obj, setObj] = useState([]);

  const handleAdd = () => {
    setClicked(true);
    setCount((prevCount) => prevCount + 1);
    setObj([
      ...obj,
      {
        id: obj.id + 1,
        startTime: ' ',
        endTime: ' ',
      },
    ]);
  };

  const handleClear = () => {
    setClicked((prev) => !prev);
    setCount(0);
    setObj([]);
  };
  const handleClearButton = (index) => {
      let newObj = obj.filter((_, i) => i !== index); // Remove the item at 'index'
      setObj(newObj);
};

  return (
    <>
      <div>
        <button type="submit" className="add" onClick={handleAdd}>
          Add new
        </button>
        <button type="submit" className="clear" onClick={handleClear}>
          Clear all
        </button>
        {clicked &&
          obj.map((ob, index) => {
            return (
                <div>
                <div>Day
                </div>
              <div className="line">
                
                
                  
                <label for="weekday" >
                <select >
                    <option value="sunday">Monday</option>
                    <option value="monday">Tuesday</option>
                    <option value="tuesday">Wednesday</option>
                    <option value="wednesday">Thursday</option>
                    <option value="thursday">Friday</option>
                    <option value="friday">Saturday</option>
                    <option value="saturday">Sunday</option>
                </select>          
                </label>
                <label for="startTime" >StartTime
                  <input type="time" className="startTime"/>{ob.startTime}
              </label>
                  <label for="endTime" >EndTime
                  <input type="time" className="endTime"/>{ob.endTime}
              </label>
                <button
                  type="submit"
                  onClick={() => {
                    handleClearButton(index);
                  }}
                >
                  Cross
                </button>
              </div>
                </div>
            );
          })}
      </div>
    </>
  );
}

export default App;
