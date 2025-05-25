class Solution {
public:
    int trap(vector<int>& height) {
       //optimal solution
        //two pointer left and end
        int n=height.size();
        int start=0,end=n-1;
        int sum=0;
        int lMax=0,rMax=0;
        //sum=sum+min(lMax,rMax)-height[index]
        while(start<end){
            if(height[start]<=height[end]){
                if(height[start]<lMax){
                    sum+=lMax-height[start];
                }
                else{
                    lMax=height[start];
                }
                start=start+1;
            }
            else{
                if(height[end]<rMax){
                    sum+=rMax-height[end];
                }
                else{
                    rMax=height[end];
                }
                end=end-1;
            }
        }
        //at the end start==end will point to largest element in height
        return sum;
    }
};
