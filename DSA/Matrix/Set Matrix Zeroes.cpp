class Solution {
public:
    void setZeroes(vector<vector<int>>& matrix) {
        int row_size=matrix.size();
        int col_size=matrix[0].size();
        int col0=1;
        for(int row=0;row<row_size;row++){
            for(int col=0;col<col_size;col++){
                if(matrix[row][col]==0){
                    matrix[row][0]=0;
                    if(col!=0)matrix[0][col]=0;
                    else col0=0;
                }
            }
        }

        for(int row=1;row<row_size;row++){
            for(int col=1;col<col_size;col++){
                if (matrix[row][0] == 0 || matrix[0][col] == 0) {
                    matrix[row][col] = 0;
                }
                
            }
        }
        if(matrix[0][0]==0){
            for(int col=0;col<col_size;col++){
                matrix[0][col]=0;
            }
        }
        if(col0==0){
            for(int row=0;row<row_size;row++){
                matrix[row][0]=0;
            }
        }
    }
};
