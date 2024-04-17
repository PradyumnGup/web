import React from "react";

const CollegeItem = ({ college }) => {
  // Destructure college data with additional columns
  const {
    // rank,
    // collegeName,
    // location,
    // course,
    // cutoff,
    // fees,
    // placement,
    // userReviews,
    // ranking,
    // applyNowUrl,
    // downloadBrochureUrl,
    // addToCompareUrl,
    id,
    collegeName,
    location,
    courseFees,
    placement,
    userReviews,
    ranking,
    applyNowUrl,
    downloadBrochureUrl,
    addToCompareUrl

  } = college;

  return (
    <tr className="college-item">
      <td className="id">#{id}</td>
      <td className="college-name"><span>{collegeName}</span>
      <div className="location">
          {location}
        </div>
        <div className="btech">
          Btech Computer Science Engineering
        </div>
      <div className="actions">
        <div className="apply">
        <a href={applyNowUrl} target="_blank" rel="noreferrer">
        &rarr; Apply Now
        </a>
        </div>
        <div className="download">
        <a  href={downloadBrochureUrl} target="_blank" rel="noreferrer">
        <i class="fas fa-arrow-down"></i>Download Brochure
        </a>
        </div>
        <div className="add">
        <a  href={addToCompareUrl} target="_blank" rel="noreferrer">
        &#43; Add To Compare
        </a>
        </div>
       
        </div>
      </td>
      {/* <td className="location">{location}</td> */}
      <td className="course">
        <div className="courseFees">
        <span>&#8377;{courseFees}</span>
        <span>BE/B.Tech </span>
        <span>-1st Year Form</span>
        </div>
        
        
        </td>
      {/* <td className="cutoff">{cutoff}</td> */}
      {/* <td className="fees">{fees}</td> */}
      
      <td >
      <div className="placement"><span>&#8377;{placement}</span>

      <span>Average Package</span>
      <span>&#8377;60000000</span>
      <span>Highest Package</span>
      
      </div>
      </td>
      
      <td className="user-reviews">{userReviews}/10</td>
      <td className="ranking">{ranking}</td>
      {/* <td className="actions">
        <a href={applyNowUrl} target="_blank" rel="noreferrer">
          Apply Now
        </a>
        <a href={downloadBrochureUrl} target="_blank" rel="noreferrer">
          Download Brochure
        </a>
        <a href={addToCompareUrl} target="_blank" rel="noreferrer">
          Add To Compare
        </a>
      </td> */}
    </tr>
  );
};

export default CollegeItem;
