class Solution {
public:
    void rotate(vector<vector<int>>& matrix) {
        int m=matrix.size();
        int n=matrix[0].size();
        //optimal solution
        //tc->O(n2/4)+ O(n*n/2) sc->O(1)
        //step1:-transpose of matrix
        //finding transpose technique

        //  1 2 3
        //  4 5 6
        //  7 8 9
        //except diagonal iterate over one half of elements and swap with other half easy O(n/2 * n/2)
        for(int i=0;i<n-1;i++){
            for(int j=i+1;j<n;j++){
                swap(matrix[i][j],matrix[j][i]);
            }
        }
        //step2:-reverse each row of matrix
            for(int i=0;i<m;i++){
                reverse(matrix[i].begin(),matrix[i].end());
            }

        //brute force
        //tc->O(n*m) sc->O(n*m)
        // vector<vector<int>>ans(m,vector<int>(n));
        //  for(int i=0;i<m;i++){
        //     for(int j=0;j<n;j++){
        //         ans[j][m-i-1]=matrix[i][j];
        //     }
        //  }
        // matrix=ans;
    }
};
