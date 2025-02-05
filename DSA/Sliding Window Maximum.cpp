class Solution {
public:
    vector<int> maxSlidingWindow(vector<int>& nums, int k) {
         using ll = long long;
        int n = nums.size(); 
        vector<int>ans;
        ll j=0;
        // priority_queue<pair<ll,ll>,vector<pair<ll,ll>>,greater<pair<ll,ll>>>pq;
        //monotonic decreasing deque
        deque<ll> dq;
        for(ll i=0; i<n; i++){
            
            while(!dq.empty() && nums[dq.back()]<nums[i]){
                dq.pop_back();
            }
            while(!dq.empty() && dq.front()<=i-k){
                dq.pop_front();
            }
            

            dq.push_back(i);
            if(i+1>=k){
                ans.push_back((int)nums[dq.front()]);
                
            }

        }
        return ans;
    }
};
