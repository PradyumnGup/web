import Navbar from "../../Components/Navbar/Navbar";
import Featured from "../../Components/Featured/Featured";
import "./home.scss";
import List from "../../Components/list/List";
import { useEffect, useState } from "react";
import axios from "axios";


const Home = ({ type }) => {
  const [lists, setLists] = useState([]);
  const [genre, setGenre] = useState(null);

  useEffect(() => {
    const getRandomLists = async () => {
      try {
        const res = await axios.get(
          `lists${type ? "?type=" + type : ""}${
            genre ? "&genre=" + genre : ""
          }`,
          {
            headers: {
              token:
              "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY0N2I3MzliZmFjYTllMGI0ZjI5NWU2MyIsImlzQWRtaW4iOmZhbHNlLCJpYXQiOjE2ODYwNzEwNzAsImV4cCI6MTY4NjUwMzA3MH0.EXz4Nv98898tzt5S_7YH17LHsEMvLoXXe5Bl7ELuGT4",
            },
          }
        );
        setLists(res.data);
      } catch (err) {
        console.log(err);
      }
    };
    getRandomLists();
  }, [type, genre]);
  
  return (
    <div className="home">
      <Navbar/>
      <Featured type = {type}/>
      {/* <List/>
      <List/>
      <List/>
      <List/> */}

      {lists.map((list) => (
        <List list={list} />
      ))}

    </div>
  )
}

export default Home
