class Solution {
public:
    int longestMonotonicSubarray(vector<int>& nums) {
          int n=nums.size();
        int maxi=INT_MIN;
        //strictly increasing
        for(int i=0;i<n;i++){
            int prev=nums[i];
            for(int j=i+1;j<n;j++){
                if(prev<nums[j]){
                    maxi=max(maxi,j-i+1);
                    prev=nums[j];
                }
                else{
                    break;
                }
            }
        }
         //strictly decreasing
        int maxi2=INT_MIN;
         for(int i=0;i<n;i++){
             int prev=nums[i];
            for(int j=i+1;j<n;j++){
                 if(prev>nums[j]){
                    maxi2=max(maxi2,j-i+1);
                    prev=nums[j];
                }
                else{
                    break;
                }
            }
        }
        if(maxi==INT_MIN && maxi2==INT_MIN)return 1;
        return max(maxi,maxi2);
    }
};
