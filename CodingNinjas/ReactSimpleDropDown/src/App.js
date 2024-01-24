import { useEffect, useState } from 'react';
import './App.css';
import DropDownList from './Components/DropDownList';
import DropDownMenu from './Components/DropDownMenu';




function App() {
  const [selected,setSelected]=useState(false);
  const [list,setList] = useState(["Yes","Probably not"]);

  const handleClick=()=>{
    setSelected(!selected);
  }

  const onClickHandler=()=>{
    setSelected(false);
  }

  
   
  return (
    <div className="App">
      <div className="dropDownBox">
          <div className="heading">
            <h2>Should you use a dropdown?</h2>
          </div>
          <DropDownMenu handleClick={handleClick}/>
          <DropDownList list= {list} selected = {selected} onClickHandler={onClickHandler}/>
      </div>
    </div>
  );
}

export default App;
