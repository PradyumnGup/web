import React from "react";
import CollegeItem from "./components/CollegeItem.js";
import "./styles.css";
let collegesData = [
  {
    id: 3,
    collegeName: "IIT Madras",
    location: "Chennai,Tamil Nadu | AICTE Approved",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 3,
  },
  {
    id: 1,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 3,
  },
  {
    id: 2,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 4,
  },
  {
    id: 4,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 7,
  },
  {
    id: 5,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 10,
  },
  {
    id: 6,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 15,
  },
  {
    id: 7,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 16,
  },
  {
    id: 8,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 18,
  },
  {
    id: 9,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 20,
  },
  {
    id: 10,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 19,
  },
  {
    id: 12,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 15,
  },
  {
    id: 11,
    collegeName: "IIT Madras",
    location: "Chennai",
    courseFees: 209550,
    placement: 2168000,
    userReviews: 8.6,
    ranking: 11,
  }
  // ... other colleges data
];



const sortByID=()=>{
  
  collegesData=collegesData.slice().sort((a, b) => a.id - b.id);
}
const sortByFees=()=>{
  
  collegesData=collegesData.slice().sort((a, b) => a.courseFees - b.courseFees);

}
const sortByUser=()=>{
  
  collegesData=collegesData.slice().sort((a, b) => a.userReviews - b.userReviews);
}

const App = () => {
  return (
    <div className="App">
      <button className="sortByID" onClick={sortByID}>SortByCollegeDuniaRank</button>
      <button className="sortByFees" onClick={sortByFees}>SortByFees</button>
      <button className="sortByUser" onClick={sortByUser}>SortByUserReviews</button>
      <table>
        <thead>
          <tr>
            <th>CD Rank</th>
            <th>Colleges</th>
            {/* <th>Location</th> */}
            <th>Course Fees</th>
            {/* <th>Cutoff</th>
            <th>Fees</th> */}
            <th>Placement</th>
            <th>User Reviews</th>
            {/* <th>Actions</th> */}
            <th>Ranking</th>
          </tr>
        </thead>
        <tbody>
          {collegesData.map((college) => (
            <CollegeItem key={college.id} college={college} />
          ))}
        </tbody>
      </table>
      
    </div>
  );
};

export default App;