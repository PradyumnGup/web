var Inputarr = [3,0,0,2,0,4];
var size = Inputarr.length;


function solve(array,size){
var mxl = [];
var mxr = [];
var water = [];
var sum =0;

mxl[0]=array[0];
for (var i=1;i<size;i++){
mxl[i]= Math.max(mxl[i-1],array[i]);


}

mxr[size-1]=array[size-1];
for (var j=size-2;j>=0;j--){
    mxr[j]=Math.max(mxr[j+1],array[j]);
    
    
    }
    
    
for(var k=0;k<size;k++){
water[k]=Math.min(mxl[k],mxr[k])-array[k];

}
for(var d=0;d<size;d++){
    sum = sum + water[d];
    
    }


return sum;

}






var water = solve(Inputarr,size);
console.log(water);