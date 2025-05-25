class Solution {
  public:
    // Function to find the minimum number of platforms required at the
    // railway station such that no train waits.
    int findPlatform(vector<int>& arr, vector<int>& dep) {
        int n=arr.size();
        sort(arr.begin(),arr.end());sort(dep.begin(),dep.end());
        int arrivalIndex=0,departureIndex=0;
        int maxPlatforms=0,cnt=0;
        while(arrivalIndex<n){
            if(arr[arrivalIndex]<=dep[departureIndex]){
                cnt++;
                arrivalIndex++;
            }
            else{
                cnt--;
                departureIndex++;
            }
            maxPlatforms=max(maxPlatforms,cnt);
        }
        return maxPlatforms;
    }
};
