//Brute force pq nlogn
class Solution {
    using int2=pair<int, int>; //(nums[i], i)
public:

    vector<int> maxSlidingWindow(vector<int>& nums, int k) {
        int n=nums.size();
        priority_queue<int2> pq;

        vector<int> ans(n-k+1);
        for (int i=0; i<k; i++)
            pq.push({nums[i], i});
        
        ans[0]=pq.top().first;
        for(int i=k; i<n; i++){
            while(!pq.empty() && pq.top().second<=(i-k))
                pq.pop(); //Pop up element not in the window
            pq.push({nums[i], i});
            ans[i-k+1]=pq.top().first;//Max element for this window
        }
        return ans;
    }
};

//Optimal deque O(n)
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
