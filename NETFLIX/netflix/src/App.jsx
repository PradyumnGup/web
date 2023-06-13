
import "./app.scss";
import Home from "./Pages/HomePage/Home";
import Login from "./Pages/LoginPage/Login";
import Register from "./Pages/RegisterPage/Register";
import Watch from "./Pages/WatchPage/Watch";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate
} from "react-router-dom";
import  { AuthContext } from "./authContext/AuthContext";
import { useContext } from "react";

const App=()=> {
  const { user } = useContext(AuthContext);
  return (
    
    <Router>
      
    <Routes>
    <Route exact path = "/" element = {user ?<Home/> : <Navigate to ="/register" replace/> }>
    </Route>
    <Route  path = "/movies" element = {<Home type = "movie"/> }>
    </Route>
    <Route  path = "/series" element = {<Home type = "series"/>}>
    </Route>
    <Route path = "/register" element = {!user ?<Register/> : <Navigate to ="/" replace/>}>
    
    </Route>
    <Route path = "/login" element = {!user ?<Login/> : <Navigate to ="/" replace/>}>
    
    </Route>
    <Route path = "/watch" element = {<Watch/>}>
    </Route>
    </Routes>
    
    </Router>
    
  );
}

export default App;
