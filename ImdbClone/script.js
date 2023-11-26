// Replace 'YOUR_OMDB_API_KEY' with your actual API key
const apiKey = '5c9e9610';
const apiUrl = 'http://www.omdbapi.com/';

function searchMovies() {
  const searchInput = document.getElementById('searchInput').value;

  if (searchInput.trim() === '') {
    alert('Please enter a movie title');
    return;
  }

  const url = `${apiUrl}?apikey=${apiKey}&s=${searchInput}`;

  fetch(url)
    .then(response => response.json())
    .then(data => {
      if (data.Response === 'True') {
        const encodedData = encodeURIComponent(JSON.stringify(data.Search));

        // Navigate to Page 2 with movie data as a query parameter
        window.location.href = `index2.html?movieData=${encodedData}`;
      } else {
        alert('No results found');
      }
    })
    .catch(error => console.error('Error fetching data:', error));
}


