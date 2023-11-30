//if items exists we parse items into json otherwise an empty array is returned
const itemsArray = localStorage.getItem('items') ? JSON.parse(localStorage.getItem('items')) : [];

// click of enter should create a todolist item
document.querySelector("#Enter").addEventListener("click", () => {
  const item = document.querySelector("#item")
  createItem(item)
})




document.querySelector("#item").addEventListener("keypress", (e) => {
  if(e.key === "Enter"){
    const item = document.querySelector("#item")
    createItem(item)
  }
})


function displayDate(){
  let date = new Date();
  //creating array of the date by converting it to string and then splitting with " "
  date = date.toString().split(" ")
  //date array created 1,2,3 index has month day and year.
  date = date[1] + " " + date[2] + " " + date[3] 
  document.querySelector("#date").innerHTML = date 
}



function displayItems(){
  let items = ""
  //adding todolist items using items array and displaying them one by one
  for(let i = 0; i < itemsArray.length; i++){
    items += `<div class="item">
                <div class="input-controller">
                  <textarea disabled>${itemsArray[i]}</textarea>
                  <div class="edit-controller">
                    <i class="fa-solid fa-check deleteBtn"></i>
                    <i class="fa-solid fa-pen-to-square editBtn"></i>
                  </div>
                </div>
                <div class="update-controller">
                  <button class="saveBtn">Save</button>
                  <button class="cancelBtn">Cancel</button>
                </div>
              </div>`
  }
  //setting it to todolist class
  document.querySelector(".to-do-list").innerHTML = items
  //Delete button code event listener on delete button click
  activateDeleteListeners()
  //Edit button event listener
  activateEditListeners()
  //Save button event listener
  activateSaveListeners()
  //Cancel button event listener
  activateCancelListeners()
}

function activateDeleteListeners(){
  //simply deleting item here
  let deleteBtn = document.querySelectorAll(".deleteBtn")
  deleteBtn.forEach((dB, i) => {
    dB.addEventListener("click", () => { deleteItem(i) })
  })
}


function activateEditListeners(){
  //taking all .editbtn nodelists here
  const editBtn = document.querySelectorAll(".editBtn")
  const updateController = document.querySelectorAll(".update-controller")
  const inputs = document.querySelectorAll(".input-controller textarea")
  editBtn.forEach((eB, i) => {
    eB.addEventListener("click", () => { 
      updateController[i].style.display = "block"
      inputs[i].disabled = false })
  })
}



function activateSaveListeners(){
  const saveBtn = document.querySelectorAll(".saveBtn")
  const inputs = document.querySelectorAll(".input-controller textarea")
  saveBtn.forEach((sB, i) => {
    sB.addEventListener("click", () => {
      updateItem(inputs[i].value, i)
    })
  })
}



function activateCancelListeners(){
  const cancelBtn = document.querySelectorAll(".cancelBtn")
  const updateController = document.querySelectorAll(".update-controller")
  const inputs = document.querySelectorAll(".input-controller textarea")
  cancelBtn.forEach((cB, i) => {
    cB.addEventListener("click", () => {
      updateController[i].style.display = "none"
      inputs[i].disabled = true
      inputs[i].style.border = "none"
    })
  })
}


function createItem(item){
  //pushing item value into an itemarray
  itemsArray.push(item.value);
  //setting item into the local storage
  localStorage.setItem('items', JSON.stringify(itemsArray));
  //reloading the page.
  location.reload();
}



function deleteItem(i){
  //splice shifts the array 1 index and deltes at the given posi
  itemsArray.splice(i,1);
  //set item in localstorage as well
  localStorage.setItem('items', JSON.stringify(itemsArray));
  //reload pages
  location.reload();
}



function updateItem(text, i){
  itemsArray[i] = text;
  //updateing the items in items array
  localStorage.setItem('items', JSON.stringify(itemsArray));
  location.reload();
}


window.onload = function() {
  //when page loads this function executes.
  displayDate();
  displayItems();
};