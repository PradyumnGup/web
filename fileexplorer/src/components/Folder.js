import  { useState } from "react";

function Folder({ handleInsertNode, explorer }){
    const [expand,setExpand] = useState(false);
    const [showInput,setShowInput] = useState({
        visible:false,
        isFolder:null
    });
    const handleClick = (e)=>{
        setExpand(!expand);
    }
    const handleNewFolder = (e,isFolder)=>{
        e.stopPropagation();
        setExpand(true);
            setShowInput({
                visible:true,
                isFolder
            })
    }
    const onAddFolder = (e)=>{
        if(e.keyCode === 13 && e.target.value){
            //add logic
            handleInsertNode(explorer.id , e.target.value , showInput.isFolder);
            setShowInput({...showInput,visible:false})
        }
    }
    if(explorer.isFolder){
    return <div style = { { marginTop:5 } }>
                <div className = "folder" onClick = {(e)=>{handleClick(e)}}>
                        <span>📁
                        { explorer.name }
                        </span>
                    <button onClick = { (e)=>handleNewFolder(e,true)}>Folder +</button>
                    <button onClick = { (e)=>handleNewFolder(e,false)}>File +</button>
                </div>
                <div style = {{display:expand ?"block":"none",paddingLeft:25}}>
                    {
                        showInput.visible && (
                            <div className ="inputContainer">
                            <span>
                                {showInput.isFolder?"📁" : "📄"}
                            </span>
                                <input 
                                    type = "text"
                                    onKeyDown ={onAddFolder}
                                    onBlur = {()=>{setShowInput({...showInput,visible:false})}}
                                    className = "inputContainer__input"
                                    autoFocus
                                    />
                            </div>
                            )
                        
                    }
                    {
                        explorer.items.map((exp)=>{
                            return(
                                // <span>{exp.name}</span>
                                <Folder handleInsertNode={ handleInsertNode } explorer = {exp} key = {exp.id}/>
                            )
                        })
                    }
                </div>
            </div>
    }
    else{
        return <span className = "file">📄{explorer.name}</span>
    }
}
export default Folder;