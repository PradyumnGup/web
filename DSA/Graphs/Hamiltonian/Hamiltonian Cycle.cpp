//TC:-O(N!)
//SC:- O(N+M)
class Solution {
public:
    bool dfs(int node, int start, int N, unordered_map<int, vector<int>>& graph, int& count, vector<int>& vis) {
        vis[node] = 1;
        count++;
        
        if (count == N) {
            // Check if there's an edge from last node back to the start
            for (int adjNode : graph[node]) {
                if (adjNode == start) return true;
            }
        }
        
        for (int adjNode : graph[node]) {
            if (!vis[adjNode]) {
                if (dfs(adjNode, start, N, graph, count, vis)) {
                    return true;
                }
            }
        }
        
        vis[node] = 0;
        count--;
        return false;
    }
    
    bool check(int N, int M, vector<vector<int>> Edges) {
        unordered_map<int, vector<int>> graph;
        for (auto edge : Edges) {
            int u = edge[0] - 1;
            int v = edge[1] - 1;
            graph[u].push_back(v);
            graph[v].push_back(u);
        }
        
        vector<int> vis(N, 0);
        int count = 0;
        
        for (int i = 0; i < N; i++) {
            if (dfs(i, i, N, graph, count, vis)) {
                return true;
            }
        }
        
        return false;
    }
};
