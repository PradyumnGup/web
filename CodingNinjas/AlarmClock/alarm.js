const alarmSubmit = document.getElementById('alarmSubmit');
//adding event listener
alarmSubmit.addEventListener('click',setAlarm);

var audio = new Audio('https://www.myinstants.com/media/sounds/baby-laughing-meme.mp3');
//function for playing alarm ringtone...
function ringBell(){
    audio.play();
}

//function runs when set alarm button is clicked
function setAlarm(e){
    e.preventDefault();
    const alarm = document.getElementById('alarm');
    alarmDate = new Date (alarm.value);
    console.log(`Setting Alarm for ${alarm.value}`);
    now =new Date();
    //finding millisecond of diff b/w now and alarmDate
    let timetoAlarm = alarmDate-now;
    console.log(timetoAlarm);
    if(timetoAlarm>=0){
        setTimeout(()=>{
            console.log("Ringing now");
            ringBell();
        },timetoAlarm);
    }
}