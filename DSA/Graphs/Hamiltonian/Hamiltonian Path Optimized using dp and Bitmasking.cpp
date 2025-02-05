//TC:-N.2^N
//SC:-N.2^N
class Solution
{
    public:
   
     bool check(int N, int M, vector<vector<int>> Edges) {
        vector<vector<int>> graph(N);
        
        // Construct adjacency list
        for (auto edge : Edges) {
            int u = edge[0] - 1;
            int v = edge[1] - 1;
            graph[u].push_back(v);
            graph[v].push_back(u);
        }
        
        // Use `int` instead of `bool` to avoid bit-reference issues
        vector<vector<int>> dp(1 << N, vector<int>(N, 0));
        
        // Base case: Start from each node
        for (int i = 0; i < N; i++) {
            dp[1 << i][i] = 1;
        }

        // Iterate over all possible subsets (mask)
        for (int mask = 0; mask < (1 << N); mask++) {
            for (int i = 0; i < N; i++) {
                if (!(mask & (1 << i))) continue; // Node i not in mask
                
                // Try to extend path to a new node j
                for (int j : graph[i]) {
                    if (mask & (1 << j)) continue; // Already visited
                    
                    int newMask = mask | (1 << j);
                    dp[newMask][j] = dp[newMask][j] || dp[mask][i];  // Use `||` instead of `|=`
                }
            }
        }

        // Check if there's any Hamiltonian path (all nodes visited)
        int fullMask = (1 << N) - 1;
        for (int i = 0; i < N; i++) {
            if (dp[fullMask][i]) return true;
        }
        return false;
    }
};
 
