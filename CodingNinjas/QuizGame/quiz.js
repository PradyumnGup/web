// const questionObj = {
//     category:'Food and Drink',
//     id:'qa-1',
//     correctAnswer:'Three',
//     options:['Two','Three','Four','Five'],
//     question:"How many pieces of bun are in a McDonald's Big Mac"
// };

const quesJSON = [
    {
      correctAnswer: 'Three ',
      options: ['Two', 'Three ', 'Four', 'Five'],
      question:
        "How many pieces of bun are in a Mcdonald's Big Mac?",
    },
    {
      correctAnswer: 'L. Frank Baum',
      options: [
        'Suzanne Collins',
        'James Fenimore Cooper',
        'L. Frank Baum',
        'Donna Leon',
      ],
      question:
        "Which author wrote 'The Wonderful Wizard of Oz'?",
    },
    {
      correctAnswer: 'Atlanta United',
      options: [
        'Atlanta United',
        'Atlanta Impact',
        'Atlanta Bulls',
        'Atlanta Stars',
      ],
      question:
        'Which of these is a soccer team based in Atlanta?',
    },
    {
      correctAnswer: 'A Nanny',
      options: [
        'A Sow',
        'A Lioness',
        'A Hen',
        'A Nanny',
      ],
      question: 'A female goat is known as what?',
    },
    {
      correctAnswer: 'P. L. Travers',
      options: [
        'J. R. R. Tolkien',
        'P. L. Travers',
        'Lewis Carroll',
        'Enid Blyton',
      ],
      question:
        "Which author wrote 'Mary Poppins'?",
    },
  ];



let score = 0;
let currentQuestion = 0;
const questionEl = document.getElementById('question');


const optionEl = document.getElementById('options');
const scoreEl = document.getElementById('score');
showQuestion();

 function showQuestion(){
    // Destructuring the object
    const{correctAnswer, options, question} = quesJSON[currentQuestion];
    const shuffledOptions = shuffleOptions(options);
    //setting text content of question id element as questionobj question.
    questionEl.textContent = question;
    //populating the options div with the buttons.
    shuffledOptions.forEach((opt)=>{
    //creating button and appending it into options div.
    const btn = document.createElement('button');
    btn.textContent = opt;
    optionEl.appendChild(btn);

    //event handling on button.
    btn.addEventListener('click',()=>{
        if(correctAnswer === opt ){
            score++;
        }
        else{
            score-=0.25;
        }
        console.log(score);
        scoreEl.textContent = `Score is ${score}`;
        nextQuestion();
    })

});
 }

//triggering next question
function nextQuestion(){
    currentQuestion++;
    optionEl.textContent='';
    if(currentQuestion>=quesJSON.length){
        questionEl.textContent = "Quiz Completed!";
        // optionEl.textContent='';
    }
    else{
        showQuestion();
    }
}

//shuffling the options
function shuffleOptions(options){
    for(let i=options.length-1;i>=0;i--){
        const j= Math.floor(Math.random()*i+1);
        [options[i],options[j]]=[options[j],options[i]];
    }
    return options;
}



// optionEl.textContent = options;
// const scoreEl = document.getElementById('score');
// scoreEl.textContent = correctAnswer;