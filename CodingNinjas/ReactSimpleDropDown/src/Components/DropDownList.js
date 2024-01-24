import React from 'react'

export default function DropDownList(props) {
  const {list,selected,onClickHandler}=props;
  return (
    <>
       {
          selected && <ul className="dropDownList" type = "none" style = {{display:`${selected?'block':'none'}`}}>
          { list.map((item,index) =>(
            <li onClick={onClickHandler}  key = {index}>{item}</li>
          ))}
          </ul> 
          
       }
    </>
  )
}

