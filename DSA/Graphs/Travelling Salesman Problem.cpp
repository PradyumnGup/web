// Time Complexity:

//     States: O(2n×n)O(2n×n)
//     Transitions: O(n)O(n) per state.
//     Total Complexity: O(n×2n)O(n×2n), which is feasible for n≤20n≤20.
// Space Complexity Analysis
// 1. DP Table Storage (dp array)

// We are using a 2D DP table dp[mask][pos], where:

//     mask has 2^n possible values (since we use bitmasking for visited cities).
//     pos (current city) has n possible values.

// So, the space required for the DP table is:
// O(2n×n)
// O(2n×n)
// 2. Recursion Stack Space

// Since we use a recursive approach, the worst-case depth of recursion is O(n) (as each recursive call moves to a new city).

// Thus, the recursion stack contributes an additional:
// O(n)
// O(n)
// Total Space Complexity
// O(2n×n+n)≈O(2n×n)
// O(2n×n+n)≈O(2n×n)

// The dominant term is O(2n×n)O(2n×n), making it the overall space complexity.
// Can We Optimize Space?

// Yes! If we use an iterative (bottom-up) DP approach instead of recursion, we can eliminate the recursion stack, reducing the space complexity to O(2^n × n).



class Solution {
public:
    int n;
    vector<vector<int>> dp;

    int solve(int mask, int pos, vector<vector<int>>& cost) {
        if (mask == (1 << n) - 1) return cost[pos][0];  // Return to starting city
        
        if (dp[mask][pos] != -1) return dp[mask][pos];

        int ans = 1e9;  // Large value for minimization

        for (int city = 0; city < n; city++) {
            if (!(mask & (1 << city))) {  // If city is unvisited
                int newMask = mask | (1 << city);
                ans = min(ans, cost[pos][city] + solve(newMask, city, cost));
            }
        }
        return dp[mask][pos] = ans;
    }

    int tsp(vector<vector<int>>& cost) {
        n = cost.size();
        dp = vector<vector<int>>(1 << n, vector<int>(n, -1));
        return solve(1, 0, cost);  // Start from city 0 with only city 0 visited
    }
};
