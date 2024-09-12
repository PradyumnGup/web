const Cell = ({filled,onClick,isDisabled,label})=>{
    return (
      <button type = 'button' 
          onClick = {onClick}
          aria-label={label}
          disabled={isDisabled}
          className = {filled?"cell cell-activated":"cell"}/>  
    );
}
export default Cell;