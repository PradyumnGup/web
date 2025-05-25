class Solution {
public:
    vector<int> spiralOrder(vector<vector<int>>& matrix) {
        int n=matrix.size(),m=matrix[0].size();
        vector<int>ans;
        int top=0,bottom = n-1;
        int left=0,right=m-1;
        while(top<=bottom && left<=right){
            int index=left,end=right;
            for(;index<=end;index++ ){
                ans.push_back(matrix[top][index]);
            }
            top++;
            
            if(top>bottom)break;
            
            index=top,end=bottom;
            for(;index<=end;index++){
                ans.push_back(matrix[index][right]);
            }
            right--;
            if(right<left)break;
            index=right,end=left;
            for(;index>=end;index--){
                ans.push_back(matrix[bottom][index]);
            }
            
            bottom--;
            if(top>bottom)break;
            index=bottom,end=top;
            for(;index>=top;index--){
             ans.push_back(matrix[index][left]);   
            }
            left++;
            
        }
        return ans;
    }
};
