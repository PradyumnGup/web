import React from 'react'

export default function DropDownMenu(props) {
  const {handleClick}=props;
  return (
    <>
      <button className="dropDownMenu" onMouseEnter = {handleClick} >
            <div className="select">Select</div>
            <div className="arrow"> &#11167;</div>
      </button>
    </>
  )
}
