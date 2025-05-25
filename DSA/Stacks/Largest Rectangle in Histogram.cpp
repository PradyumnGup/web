class Solution {
public:
    int largestRectangleArea(vector<int>& heights) {
        int n=heights.size();
        int maxAreaHist=INT_MIN;
        //pse and nse at each index
        stack<int>stack;
        for(int i=0;i<n;i++){
            while(!stack.empty() && heights[stack.top()]>heights[i]){
                int valI=stack.top();
                stack.pop();
                
                //nse of val is heights[i]and pse of val is top value in stack or if empty then -1
                int nse=i;
                int pse=(stack.empty()?-1:stack.top());
                maxAreaHist=max(maxAreaHist,heights[valI]*(nse -pse-1));
                
            }
            stack.push(i);
        }
        //leftover elements
        while(!stack.empty()){
            int elI=stack.top();
            stack.pop();
            int nse=n;
            int pse=(stack.empty()?-1:stack.top());
            maxAreaHist=max(maxAreaHist,heights[elI]*(nse -pse-1));
        }
        return maxAreaHist;
    }
};
