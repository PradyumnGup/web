#include <bits/stdc++.h>
using namespace std;

// Free function comparator: orders pairs by second value
static bool cmp(const pair<int,int>& a, const pair<int,int>& b) {
    if (a.second != b.second)
        return a.second < b.second;   // primary: second value
    return a.first < b.first;         // tie-breaker: first value
}

int main() {
    // Note: set type includes function pointer
    set<pair<int,int>,bool(*)(const pair<int,int>& a, const pair<int,int>& b)> s(cmp);

    s.insert({5,10});
    s.insert({2,15});
    s.insert({3,10});
    s.insert({1,20});

    // Iterate
    for(auto p : s) {
        cout << "(" << p.first << "," << p.second << ") ";
    }
    cout << "\n";

    // Lower bound on second value
    pair<int,int> query = {INT_MIN, 15};
    auto it = s.lower_bound(query);
    if(it != s.end()) {
        cout << "Lower bound: (" << it->first << "," << it->second << ")\n";
    }
}
