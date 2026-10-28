class Solution {
public:
    vector<vector<int>> generate(int numRows) {
        vector<vector<int>> answer;
        answer.push_back({1});
        
        for(int i=1;i<numRows;i++){
            vector<int>helper(i+1,1);
            
            for(int j=1;j<i;j++){
                helper[j]=answer[i-1][j-1]+answer[i-1][j];
                
            }
            answer.push_back(helper);
            
        }
        return answer;
    }
};
