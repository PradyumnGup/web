#include<bits/stdc++.h>
using namespace std;

#define ll long long
vector<char>path;
bool bfs(int row,int col,vector<vector<char>>grid,vector<vector<char>>&dir){
    int n = grid.size(), m = grid[0].size();
    queue<pair<int, int>> q;
    q.push({row, col});
    vector<vector<bool>> visited(n, vector<bool>(m, false));
    visited[row][col] = true;

    // Direction vectors and mapping
    vector<pair<int, int>> directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    vector<char> dir_map = {'R', 'L', 'D', 'U'};

    while (!q.empty()) {
        auto [x, y] = q.front();
        q.pop();

        // Check if we've reached 'B'
        if (grid[x][y] == 'B') {
            // Reconstruct path
            while (grid[x][y] != 'A') {
                path.push_back(dir[x][y]);
                char d = path.back();
                if (d == 'L') y++;
                else if (d == 'R') y--;
                else if (d == 'U') x++;
                else if (d == 'D') x--;
            }
            reverse(path.begin(), path.end());
            cout << "YES\n";
            cout << path.size() << '\n';
            for (char c : path) cout << c;
            cout << '\n';
            return true;
        }

        // Explore neighbors
        for (int i = 0; i < 4; i++) {
            int nx = x + directions[i].first;
            int ny = y + directions[i].second;

            if (nx >= 0 && ny >= 0 && nx < n && ny < m && grid[nx][ny] != '#' && !visited[nx][ny]) {
                visited[nx][ny] = true;
                q.push({nx, ny});
                dir[nx][ny] = dir_map[i];
            }
        }
    }
    return false;
}
bool findLabyrinth(vector<vector<char>>grid){
    ll n=grid.size(),m=grid[0].size();
    vector<vector<char>>dir(n,vector<char>(m));
    for(ll i=0;i<n;i++){
        for(ll j=0;j<m;j++){
            if(grid[i][j]=='A'){
                if(bfs(i,j,grid,dir)){
                    return true;   
                }
                else{
                    return false;
                }
            }
        }
    }
    
    return false;
}

int main(){
    ll n,m;
    cin>>n>>m;
    vector<vector<char>>grid(n,vector<char>(m));
    for(ll i=0;i<n;i++){
        for(ll j=0;j<m;j++){
            char val;
            cin>>val;
            grid[i][j]=val;
        }
    }
    if(!findLabyrinth(grid)){
        cout<<"NO"<<endl;
    }
    return 0;
}
