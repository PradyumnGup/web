class Solution {
public:
    int tsp(vector<vector<int>>& cost) {
        int n = cost.size();
        
        // Fixing the edge case for n == 1
        if (n == 1) return cost[0][0];

        int fullMask = (1 << n) - 1;
        vector<vector<int>> dp(1 << n, vector<int>(n, 1e9));

        // Base Case: Starting from city 0 with only city 0 visited
        dp[1][0] = 0;  

        // Iterate over all subsets of visited cities
        for (int mask = 1; mask <= fullMask; mask++) {
            for (int pos = 0; pos < n; pos++) {
                if (!(mask & (1 << pos))) continue;  // Skip if `pos` is not in the visited set

                // Try to transition to an unvisited city
                for (int nextCity = 0; nextCity < n; nextCity++) {
                    if (mask & (1 << nextCity)) continue; // Skip already visited cities

                    int newMask = mask | (1 << nextCity);
                    dp[newMask][nextCity] = min(dp[newMask][nextCity], dp[mask][pos] + cost[pos][nextCity]);
                }
            }
        }

        // Compute final answer: minimum cost to visit all cities and return to 0
        int minCost = 1e9;
        for (int lastCity = 1; lastCity < n; lastCity++) {
            minCost = min(minCost, dp[fullMask][lastCity] + cost[lastCity][0]);
        }

        return minCost;
    }
};
Time & Space Complexity
Aspect	Complexity
Time Complexity	O(2n×n2)O(2n×n2)
Space Complexity	O(2n×n)O(2n×n)
