class Solution {
public:
    void mergeTwoSortedArrays(int low,int mid,int high,vector<int>&nums){
        int i=low,j=mid+1;
        vector<int>sorted(high-low+1,0);
        int k=0;
        while(i<=mid && j<=high){
            if(nums[i]<=nums[j]){
                sorted[k]=nums[i];
                k++;
                i++;
            }
            else{
                sorted[k]=nums[j];
                k++;
                j++;
            }
        }
        
        while(i<=mid){
            sorted[k]=nums[i];
            k++;
            i++;
        }
         while(j<=high){
            sorted[k]=nums[j];
            k++;
            j++;
        }
        //sorted array combined two arrays sorted
        for(int l=0;l<sorted.size();l++){
            nums[low+l]=sorted[l];
        }
        return;
    }
    
    void mergeSort(int low,int high,vector<int>&nums){
        if(low==high)return;
        
        int mid=low+(high-low)/2;//(high+low)/2
        mergeSort(low,mid,nums);
        mergeSort(mid+1,high,nums);
        mergeTwoSortedArrays(low,mid,high,nums);
        
        return;
    }
    vector<int> sortArray(vector<int>& nums) {
        int n=nums.size();
        mergeSort(0,n-1,nums);
        return nums;
    }
};
            //[5,2,3,1]
   // [5,2]  [2,5]            [1,3]     [3,1]
//[5]       [2]             [3]     [1]


//[2,5]  [1,3]
//     i      j  
//[1,2,3,5]
//   k
     
