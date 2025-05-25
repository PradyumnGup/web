class Solution {
public:
    static bool cmp(vector<int>&a,vector<int>&b){
        return a[1]<b[1];
    }
    int eraseOverlapIntervals(vector<vector<int>>& intervals) {
        int n=intervals.size();
        sort(intervals.begin(),intervals.end(),cmp);
        int freeTime=intervals[0][1];
        int cntRemove=0;
        for(int i=1;i<n;i++){
            int startTime=intervals[i][0];
            if(startTime<freeTime){
                //overlapping
                cntRemove++;

            }
            else{
                freeTime=intervals[i][1];
            }
        }
        return cntRemove;
    }
};
//[[1,2][1,3][2,3][3,4]]
