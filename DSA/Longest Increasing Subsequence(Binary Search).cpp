int lengthOfLIS(vector<int>& nums) {
        int n=nums.size();
      //O(nlogn)binary search
        vector<int>temp;
        temp.push_back(nums[0]);
        for(int i=1;i<n;i++){
            if(nums[i]>temp.back()){
                temp.push_back(nums[i]);
            }
            else{
                auto index=lower_bound(temp.begin(),temp.end(),nums[i]);
                *index=nums[i];
            }
        }
        return temp.size();

}
