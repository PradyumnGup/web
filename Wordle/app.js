
// import { WORDS } from "./words.js";
const WORDS = [
    'which',
    'there',
    'their',
    'about',
    'would',
    'these',
    'other',
    'words',
    'could',
    'write',
    'first',
    'water',
    'after',
    'where',
    'right',
    'think',
    'three',
    'years',
    'place',
    'sound',
    'great',
    'again',
    'still',
    'every',
    'small',
    'found',
    'those',
    'never',
    'under',
    'might',
    'while',
    'house',
    'world',
    'below',
    'asked',
    'going',
    'large',
    'until',
    'along',
    'shall',
    'being',
    'often',
    'earth',
    'began',
    'since',
    'study',
    'night',
    'light',
    'above',
    'paper',
    'parts',
    'young',
    'story',
    'point',
    'times',
    'heard',
    'whole',
    'white',
    'given',
    'means',
    'music',
    'miles',
    'thing',
    'today',
    'later',
    'using',
    'money',
    'lines',
    'order',
    'group',
    'among',
    'learn',
    'known',
    'space',
    'table',
    'early',
    'trees',
    'short',
    'hands',
    'state',
    'black',
    'shown',
    'stood',
    'front',
    'voice',
    'kinds',
    'makes',
    'comes',
    'close',
    'power',
    'lived',
    'vowel',
    'taken',
    'built',
    'heart',
    'ready',
    'quite',
    'class',
    'bring',
    'round',
    'horse',
    'shows',
    'piece',
    'green',
    'stand',
    'birds',
    'start',
    'river',
    'tried',
    'least',
    'field',
    'whose',
    'girls',
    'leave',
    'added',
    'color',
    'third',
    'hours',
    'moved',
    'plant',
    'doing',
    'names',
    'forms',
    'heavy',
    'ideas',
    'cried',
    'check',
    'floor',
    'begin',
    'woman',
    'alone',
    'plane',
    'spell',
    'watch',
    'carry',
    'wrote',
    'clear',
    'named',
    'books',
    'child',
    'glass',
    'human',
    'takes',
    'party',
    'build'];
const tileDisplay=document.querySelector('.tile-container');
const keyboard=document.querySelector('.key-container');
const messageDisplay=document.querySelector('.message-container');

let wordle=WORDS[Math.floor(Math.random()*WORDS.length)];
wordle=wordle.toUpperCase();
console.log(wordle);
// const getWordle = () => {
//     fetch('http://localhost:8000/word')
//         .then(response => response.json())
//         .then(json => {
//             wordle = json.toUpperCase()
//         })
//         .catch(err => console.log(err))
// }
// getWordle()

const keys = [
    'Q',
    'W',
    'E',
    'R',
    'T',
    'Y',
    'U',
    'I',
    'O',
    'P',
    'A',
    'S',
    'D',
    'F',
    'G',
    'H',
    'J',
    'K',
    'L',
    'ENTER',
    'Z',
    'X',
    'C',
    'V',
    'B',
    'N',
    'M',
    '«',
]

const guessRows = [
    ['', '', '', '', ''],
    ['', '', '', '', ''],
    ['', '', '', '', ''],
    ['', '', '', '', ''],
    ['', '', '', '', ''],
    ['', '', '', '', '']
]

// console.log(keyboard);
let currrow=0;
let currtile=0;
let isGameOver=false;
// let length=0;

guessRows.forEach((guessRow,guessRowIndex)=>{
const rowElement=document.createElement('div');
rowElement.setAttribute('id','guessRow-'+ guessRowIndex);
guessRow.forEach((guess, guessIndex)=>{
const tileElement =document.createElement('div');
tileElement.setAttribute('id','guessRow-' + guessRowIndex + '-tile-' + guessIndex);
tileElement.classList.add('tile');
rowElement.append(tileElement);
})
tileDisplay.append(rowElement);
})
document.addEventListener('keyup',(e)=> handleClick(e.key.toUpperCase()));
keys.forEach((key) => {
    const buttonElement = document.createElement('button');
    buttonElement.textContent = key;
    buttonElement.setAttribute('id',key);
    buttonElement.addEventListener('click',()=> handleClick(key));
    // buttonElement.addEventListener('keyup',()=> handleClick(key));
    keyboard.append(buttonElement);
    // console.log(keyboard);
    // console.log(buttonElement);
});

const handleClick=(key)=>{
    if(!isGameOver){
        if(key=='«'|| key=='BACKSPACE'){
            deleteLetter();
            return;
        }
        if(key=='ENTER'|| key=='ENTER'){
           checkRow();
            return;
        }
        if(key=='ALT'|| key=='CAPSLOCK'|| key=='PAGEDOWN' || key=='NUMLOCK'|| key=='SHIFT'){
            // if(len==1){
            //     currtile;
            // }
             return;
         }
        addLetter(key);
    }
    
}

const addLetter = (key)=>{
    if(currrow <= 5 && currtile <= 4){
        const tile = document.getElementById('guessRow-' + currrow + '-tile-' + currtile);
        tile.textContent=key;
        guessRows[currrow][currtile]=key;
        tile.setAttribute('data',key);
        currtile++;
    }
}

const deleteLetter=()=>{
   if(currtile > 0){

    currtile--;
    const tile = document.getElementById('guessRow-' + currrow + '-tile-' + currtile); 
    tile.textContent='';
    guessRows[currrow][currtile]='';
    tile.setAttribute('data','');
   }

}

const checkRow = ()=>{
    const guess=guessRows[currrow].join('');
    if(!WORDS.includes(guess.toLowerCase())){
        showMessage('Not a valid word');
        // isGameOver = true;
        return;  
    }
    if(currtile > 4){
        console.log('guess is'+guess,'wordle id'+wordle);
    
        flipTile();
        if(wordle===guess){
            showMessage('Magnificient!');
            isGameOver = true;
            return;
        }
        else{
            if(currrow>=5){
                showMessage('Game Over');
            isGameOver = true;
            return;
            }
            if(currrow < 5){
                currrow++;
                currtile = 0;
            }
        }
    }
}

const showMessage = (message) => {
    const messageElement = document.createElement('p')
    messageElement.textContent = message
    messageDisplay.append(messageElement)
    setTimeout(() => messageDisplay.removeChild(messageElement), 2000);

}
const addColorToKey = (keyLetter, color) => {
    const key = document.getElementById(keyLetter)
    key.classList.add(color)
}

const flipTile = () => {
    const rowTiles = document.querySelector('#guessRow-' + currrow).childNodes
    let checkWordle = wordle
    const guess = []

    rowTiles.forEach(tile => {
        guess.push({letter: tile.getAttribute('data'), color: 'grey-overlay'})
    })

    guess.forEach((guess, index) => {
        if (guess.letter == wordle[index]) {
            guess.color = 'green-overlay'
            checkWordle = checkWordle.replace(guess.letter, '')
        }
    })

    guess.forEach(guess => {
        if (checkWordle.includes(guess.letter)) {
            guess.color = 'yellow-overlay'
            checkWordle = checkWordle.replace(guess.letter, '')
        }
    })

    rowTiles.forEach((tile, index) => {
        setTimeout(() => {
            tile.classList.add('flip')
            tile.classList.add(guess[index].color)
            addColorToKey(guess[index].letter, guess[index].color)
        }, 500 * index)
    })
}
