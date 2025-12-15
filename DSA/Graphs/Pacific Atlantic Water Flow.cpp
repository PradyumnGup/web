//Brute force O(n*m)*(n*m)
class Solution {
public:
    void dfs(int row,int col,vector<vector<int>>&heights,
             vector<vector<int>>&vis,bool &pacific,bool &atlantic){
        int n=heights.size(),m=heights[0].size();  
        vis[row][col]=1;
        int delR[]={-1,+1,0,0};
        int delC[]={0,0,-1,+1};
        for(int k=0;k<4;k++){
            int nrow=row+delR[k];
            int ncol=col+delC[k];
              if(ncol==m || nrow==n){
                atlantic=true;
            }
            if(nrow==-1 || ncol==-1){
                pacific=true;
            }
            if(nrow<n && ncol<m && nrow>=0 && ncol>=0 && !vis[nrow][ncol] && 
               heights[nrow][ncol]<=heights[row][col]){
                dfs(nrow,ncol,heights,vis,pacific,atlantic);
            }
        }
        vis[row][col]=0;
        return;
    }
    vector<vector<int>> pacificAtlantic(vector<vector<int>>& heights) {
        int n=heights.size(),m=heights[0].size();
        vector<vector<int>>ans;
        vector<vector<int>>vis(n,vector<int>(m,0));
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                bool pacific=false,atlantic=false;
                dfs(i,j,heights,vis,pacific,atlantic);
                if(pacific && atlantic){
                    ans.push_back({i,j});
                }
            }
        }
        return ans;
    }
};

//Optimal Solution
//O(n*m)
class Solution {
public:
    void dfs(int row,int col,vector<vector<int>>&heights,
             vector<vector<int>>&vis){
        int n=heights.size(),m=heights[0].size();  
        vis[row][col]=1;
        int delR[]={-1,+1,0,0};
        int delC[]={0,0,-1,+1};
        for(int k=0;k<4;k++){
            int nrow=row+delR[k];
            int ncol=col+delC[k];
            if(nrow<n && ncol<m && nrow>=0 && ncol>=0 && !vis[nrow][ncol] && 
               heights[nrow][ncol]>=heights[row][col]){
                dfs(nrow,ncol,heights,vis);
            }
        }
        
        return;
    }
    vector<vector<int>> pacificAtlantic(vector<vector<int>>& heights) {
        int n=heights.size(),m=heights[0].size();
        vector<vector<int>>ans;
        vector<vector<int>>visPacific(n,vector<int>(m,0));
        vector<vector<int>>visAtlantic(n,vector<int>(m,0));
        for(int i=0;i<n;i++){ 
            dfs(i,0,heights,visPacific);
            dfs(i,m-1,heights,visAtlantic);
        }
        for(int i=0; i<m; i++){
            dfs(0,i,heights,visPacific);
            dfs(n-1,i,heights,visAtlantic);
        }
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                if(visPacific[i][j]==1 && visAtlantic[i][j]==1){
                    ans.push_back({i,j});
                }
            }
        }
        return ans;
    }
};
//brute force of going onto each cell gives tle
//You have to think of a solution whichdosen't goes on each cell to check if it reache to pacific and atlantic ocean
//take top and left column for pacific and mark all visited cells you can reach from it
