class Solution {
public:
    int singleNonDuplicate(vector<int>& nums) {
        int n=nums.size();

        if(n==1)return nums[0];
        if(nums[0]!=nums[1])return nums[0];
        if(nums[n-2]!=nums[n-1])return nums[n-1];

        int start=0,end=n-1;
        while(start<=end){
            int mid=start+(end-start)/2;

            if(nums[mid-1]!=nums[mid] && nums[mid+1]!=nums[mid]){
                return nums[mid];
            }
            //either left will be equal or right will be equal
            //left is equal only
            else if(nums[mid+1]!=nums[mid]){
                int leftCount=mid-(start)+1;
                if(leftCount%2==0){
                    start=mid+1;
                }
                else{
                    end=mid-2;
                }
            }
            //right is equal only
            else {
                int rightCount=(end)-mid+1;
                if(rightCount%2==0){
                    end=mid-1;
                }
                else{
                    start=mid+2;
                }
            }
        }
        return -1;
    }
};
//1 1 2 2  4 4 5 5 3
