class Solution {
  public:
    // Function to sort an array using quick sort algorithm.
    void quickSort(vector<int>& arr, int low, int high) {
        if(low<high){
            int partInd=partition(arr,low,high);
            quickSort(arr,low,partInd-1);
            quickSort(arr,partInd+1,high);
        }
    }

  public:
    // Function that takes last element as pivot, places the pivot element at
    // its correct position in sorted array, and places all smaller elements
    // to left of pivot and all greater elements to right of pivot.
    int partition(vector<int>& nums, int low, int high) {
        int pivot=nums[low];
        int i=low,j=high;
        while(i<j){
            while(nums[i]<=pivot && i<=high-1){
                i++;
            }
            while(nums[j]>pivot && j>=low+1){
                j--;
            } 
            if(i<j)swap(nums[i],nums[j]);
        }
        swap(nums[low],nums[j]);
        return j;
    }
};
