class Solution {
public:
    bool search(vector<int>& nums, int target) {
       int n=nums.size();
        int low=0,high=n-1;
        while(low<=high){
            int mid=low+(high-low)/2;
            
            if(nums[mid]==target){
                return true;
            }
            else if(nums[low]==nums[mid] && nums[mid]==nums[high]){
                low++;
                high--;
            }
            //right half unsorted
            else if(nums[high]>=nums[mid]){
                if(nums[mid]<=target && nums[high]>=target){
                    low=mid+1;
                }
                else{
                    high=mid-1;
                }
            }
            //left half unsorted
            else{
                 if(nums[low]<=target && nums[mid]>=target){
                    high=mid-1;
                    
                }
                else{
                    low=mid+1;
                }
            }
        }
        return false;
    }
};

//[3,1,2,3,3,3,3]
//low   mid   high
//right half sorted but program can't tell bcoz all equal
