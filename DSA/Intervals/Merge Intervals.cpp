vector<vector<int>> mergeIntervals(vector<vector<int>>& intervals) {
      int n=intervals.size();
      sort(intervals.begin(),intervals.end());
      vector<vector<int>>ans;
      
      for(int i=0;i<n;i++){
          if(ans.empty() || intervals[i][0]>ans.back()[1]){
              ans.push_back(intervals[i]);
          }
          else{
              ans.back()={min(ans.back()[0],intervals[i][0]),max(ans.back()[1],intervals[i][1])};
          }
      }
      return ans;
}
