var stack1 = [];
var stack2 = [];
var arr1 = [];
var arr2 = [];
var Array = [2,4];
var size = Array.length;
var area = [];
var width = [];
var psuedoIndex = -1;

// function solve(array){
// for(var i=size-1;i>=0;i--){
// if(stack.length == 0){
//     arr.push(-1);
// }
// else if(stack.length>0 && stack[stack.length-1]>array[i]){
// arr.push(stack[stack.length-1]);
// }
// else if(stack.length>0 && stack[stack.length-1]<=array[i]){
// while(stack.length>0 && stack[stack.length-1]<=array[i]){
//     stack.pop();
// }
// if(stack.length == 0){
//     arr.push(-1);
// }
// else {
//     arr.push(stack[stack.length-1]);
// }

// }
// stack.push(array[i]);
// }
// arr.reverse();
// return arr;
// }


// function solve1(array){
//     for(var i=0;i<size;i++){
//     if(stack1.length == 0){
//         arr.push(psuedoIndex);
//     }
//     else if(stack1.length>0 && stack1[stack1.length-1]>array[i]){
//     arr.push(stack2[stack2.length-1]);
//     }
//     else if(stack1.length>0 && stack1[stack1.length-1]<=array[i]){
//     while(stack1.length>0 && stack1[stack1.length-1]<=array[i]){
//         stack1.pop();
//         stack2.pop();
//     }
//     if(stack1.length == 0){
//         arr.push(-1);
//     }
//     else {
//         arr.push(stack2[stack2.length-1]);
//     }
    
//     }
//     stack1.push(array[i]);
//     stack2.push(i);
//     }

//     for(j=0;j<arr.length;j++){
//         arr[j]=j-arr[j];
//     }
//     return arr;
//     }

    function solve2(array){
        stack1=[];
            stack2=[];
            psuedoIndex = -1;
        for(var k=0;k<size;k++){
        if(stack1.length == 0){
            arr2.push(psuedoIndex);
        }
        else if(stack1.length>0 && stack1[stack1.length-1]<array[k]){
        arr2.push(stack2[stack2.length-1]);
        }
        else if(stack1.length>0 && stack1[stack1.length-1]>=array[k]){
        while(stack1.length>0 && stack1[stack1.length-1]>=array[k]){
            stack1.pop();
            stack2.pop();
        }
        if(stack1.length == 0){
            arr2.push(-1);
        }
        else {
            arr2.push(stack2[stack2.length-1]);
        }
        
        }
        stack1.push(array[k]);
        stack2.push(k);
        }
    
        // for(j=0;j<arr.length;j++){
        //     arr[j]=j-arr[j];
        // }
        return arr2;
        }

        function solve3(array){
            psuedoIndex = array.length;
            
            for(var i=size-1;i>=0;i--){
            if(stack1.length == 0){
                arr1.push(psuedoIndex);
            }
            else if(stack1.length>0 && stack1[stack1.length-1]<array[i]){
            arr1.push(stack2[stack2.length-1]);
            }
            else if(stack1.length>0 && stack1[stack1.length-1]>=array[i]){
            while(stack1.length>0 && stack1[stack1.length-1]>=array[i]){
                stack1.pop();
                debugger;
                stack2.pop();
            }
            if(stack2.length == 0){
                arr1.push(-1);
            }
            else {
                arr1.push(stack2[stack2.length-1]);
            }
            
            }
            stack1.push(array[i]);
            stack2.push(i);
            }
            arr1.reverse();
        
            // for(j=0;j<arr.length;j++){
            //     arr[j]=j-arr[j];
            // }
            return arr1;
            }
var right = solve3(Array);
var left = solve2(Array);


  for(j=0;j<Array.length;j++){
width[j] =right[j]-left[j]-1;
area[j] = Array[j]*width[j];
}

console.log( Math.max.apply(null, area)); 