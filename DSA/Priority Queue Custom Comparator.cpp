#include <bits/stdc++.h>
using namespace std;

struct cmp {
    bool operator()(const pair<int,int>& a,
                    const pair<int,int>& b) const {
        if (a.second != b.second)
            return a.second > b.second;   // min-heap by second
        return a.first > b.first;         // tie-breaker
    }
};

int main() {
    priority_queue<pair<int,int>,
                   vector<pair<int,int>>,
                   cmp> pq;

    pq.push({5,10});
    pq.push({2,15});
    pq.push({3,10});
    pq.push({1,20});

    while(!pq.empty()) {
        auto p = pq.top();
        pq.pop();
        cout << "(" << p.first << "," << p.second << ") ";
    }
}
