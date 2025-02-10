class Solution {
public:
    int longest = -1;

    void dfs(int node, vector<int>& edges, vector<int>& visited, vector<int>& depth, int currentDepth) {
        if (node == -1) return; // No outgoing edge
        
        if (visited[node] == 1) {  // Cycle detected
            longest = max(longest, currentDepth - depth[node]);
            return;
        }
        
        if (visited[node] == 2) return; // Already processed
        
        // Mark node as visited and store depth
        visited[node] = 1;
        depth[node] = currentDepth;

        // Recur for the next node
        dfs(edges[node], edges, visited, depth, currentDepth + 1);

        // Mark node as fully processed
        visited[node] = 2;
    }
    int longestCycle(vector<int>& edges) {
        int n = edges.size();
        vector<int> visited(n, 0); // 0: unvisited, 1: visiting, 2: processed
        vector<int> depth(n, -1);

        for (int i = 0; i < n; ++i) {
            if (visited[i] == 0) {
                dfs(i, edges, visited, depth, 0);
            }
        }

        return longest;
    }
};
