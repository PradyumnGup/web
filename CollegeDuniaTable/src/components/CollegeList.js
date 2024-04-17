import React from "react";
import CollegeItem from "./CollegeItem.js";
import { InfiniteScroll } from "react-infinite-scroll-component";

const CollegeList = ({ colleges }) => {
    const [colleges, setColleges] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);

    const fetchMoreData = async () => {
        // Simulate fetching data for next page (replace with your actual API call)
        const newPageColleges = [
          {
            id: 99,
            collegeName: "IIIT Madras",
            location: "Chennai",
            courseFees: 209550,
            placement: 2168000,
            userReviews: 8.6,
            ranking: 3,
          },
          {
            id: 100,
            collegeName: "IIIT Madras",
            location: "Chennai",
            courseFees: 209550,
            placement: 2168000,
            userReviews: 8.6,
            ranking: 3,
          },
        ];
      
        setColleges((prevColleges) => [...prevColleges, ...newPageColleges]);
        setCurrentPage(currentPage + 1);
      
        // Resolve the promise to indicate successful data fetching (optional)
        return newPageColleges;
      };

  return (
    <div className="college-list">
    <h2>List of Colleges</h2>
    <InfiniteScroll
      dataLength={colleges.length} // Total number of colleges loaded
      next={fetchMoreData} // Function to fetch data for next page
      hasMore={true} // Indicate if there's more data to load
      loader={<p>Loading...</p>} // Loading indicator while fetching data
    >
    <table>
      <thead>
        <tr>
          <th>College Name</th>
          <th>Location</th>
          <th>Course Fees</th>
          <th>Placement</th>
          <th>User Reviews</th>
          <th>Ranking</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {colleges.map((college) => (
          <CollegeItem key={college.id} college={college} />
        ))}
      </tbody>
    </table>
    </InfiniteScroll>
    </div>
  );
};

export default CollegeList;