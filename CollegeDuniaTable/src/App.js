import React, { useEffect, useState } from "react";
import CollegeItem from "./components/CollegeItem.js";
import "./styles.css";
let collegesData = [
  {
    id: 3,
    collegeName: "IIT Madras",
    location: "Chennai,Tamil Nadu | AICTE Approved",
    courseFees: 209551,
    placement: 2168000,
    userReviews: 9.6,
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





const App = () => {
  const [collegeData,setCollegeData]=useState(collegesData);
  const [sortBy, setSortBy] = useState(null);
  const sortData = (sortByParam) => {
    let sortedData = [...collegesData]; // Create a copy of the array

    if (sortByParam === "id") {
      sortedData.sort((a, b) => a.id - b.id);
    } else if (sortByParam === "fees") {
      sortedData.sort((a, b) => a.courseFees - b.courseFees);
    } else if (sortByParam === "user") {
      sortedData.sort((a, b) => a.userReviews - b.userReviews);
    }

    setCollegeData(sortedData);
    setSortBy(sortByParam);
  };

  useEffect(() => {
    if (sortBy !== null) {
      sortData(sortBy);
    }
  }, [sortBy]);

  return (
    <div className="App">
      <button className="sortByID" onClick={() => sortData("id")}>SortByCollegeDuniaRank</button>
      <button className="sortByFees" onClick={() => sortData("fees")}>SortByFees</button>
      <button className="sortByUser" onClick={() => sortData("user")}>SortByUserReviews</button>
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
          {collegeData.map((coll) => (
            <CollegeItem key={coll.id} college={coll} />
          ))}
        </tbody>
      </table>
      
    </div>
  );
};

export default App;