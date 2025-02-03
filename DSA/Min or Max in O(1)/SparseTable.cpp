#include <iostream>
#include <vector>
#include <cmath>

using namespace std;

class SparseTable {
private:
    vector<vector<int>> minTable, maxTable;
    vector<int> logValues;
    int n;

public:
    SparseTable(const vector<int>& arr) {
        n = arr.size();
        int maxLog = log2(n) + 1;
        minTable.assign(n, vector<int>(maxLog));
        maxTable.assign(n, vector<int>(maxLog));
        logValues.resize(n + 1);

        // Precompute log values
        for (int i = 2; i <= n; i++)
            logValues[i] = logValues[i / 2] + 1;

        // Initialize sparse tables
        for (int i = 0; i < n; i++) {
            minTable[i][0] = arr[i];
            maxTable[i][0] = arr[i];
        }

        // Build tables
        for (int j = 1; (1 << j) <= n; j++) {
            for (int i = 0; i + (1 << j) <= n; i++) {
                minTable[i][j] = min(minTable[i][j - 1], minTable[i + (1 << (j - 1))][j - 1]);
                maxTable[i][j] = max(maxTable[i][j - 1], maxTable[i + (1 << (j - 1))][j - 1]);
            }
        }
    }

    int queryMin(int L, int R) {
        int j = logValues[R - L + 1];
        return min(minTable[L][j], minTable[R - (1 << j) + 1][j]);
    }

    int queryMax(int L, int R) {
        int j = logValues[R - L + 1];
        return max(maxTable[L][j], maxTable[R - (1 << j) + 1][j]);
    }
};

int main() {
    vector<int> arr = {1, 3, 2, 7, 9, 11, 3, 5, 8};
    SparseTable st(arr);
    
    cout << "Min in range [1, 4]: " << st.queryMin(1, 4) << endl;
    cout << "Max in range [2, 6]: " << st.queryMax(2, 6) << endl;
    cout << "Min in range [0, 8]: " << st.queryMin(0, 8) << endl;
    cout << "Max in range [3, 7]: " << st.queryMax(3, 7) << endl;
    
    return 0;
}
