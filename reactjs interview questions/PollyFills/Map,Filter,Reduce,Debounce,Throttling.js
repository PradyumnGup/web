const arr=[1,2,3,4,5];


Array.prototype.myReducer=function(cb,initialVal){
    let acc=0;
    if(initialVal)acc=initialVal;
    for(let i=0;i<this.length;i++){
        acc=cb(acc,this[i],i,arr);
    }
    return acc;
}
const reducedVal=arr.myReducer((acc,val,i,arr)=>{
    return acc+val;
},0);

console.log(reducedVal);
Array.prototype.myMap=function(cb){
    let temp=[];
    
    for(let i=0;i<this.length;i++){
        temp.push(cb(this[i],i,arr));
    }
    return temp;
}
const arr2=arr.myMap((val,i,arr)=>{
    return val*2;
},0);

console.log(arr2);

Array.prototype.myFilter=function(cb){
    let temp=[];
    
    for(let i=0;i<this.length;i++){
        if(cb(this[i],i,arr)){
            temp.push(this[i]);
        }
    }
    return temp;
}
const arr2=arr.myFilter((val,i,arr)=>{
    return val!=1;
});

console.log(arr2);


const myDebounce=function(cb,d){
    let timer;
    return function(...args){
        if(timer)clearTimeout(timer);
        timer=setTimeout(()=>{
            cb(...args)
        },d);
    }
}
const debounce=myDebounce(()=>{
    
},800);
debounce();


const myThrottle=function(cb,d){
    let last=0;
    return function(...args){
        let now=new Date().getTime();
        if(now-last<d)return;
        last=now;
        return cb(...args);
    }
}
const throttle=myThrottle(()=>{
    
},800);
throttle();
