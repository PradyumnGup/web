class DisjointSet
{
private:
    

public:
    vector<int> rank, parent, size;
    // stack<pair<int, int>> history;
    DisjointSet(int n)
    {
        rank.resize(n + 1, 0);
        parent.resize(n + 1);
        size.resize(n + 1);
        for (int i = 0; i < n; i++)
        {
            parent[i] = i;
            size[i] = 1;
        }
    }
    int findPar(int node)
    {
        if (node == parent[node])
            return node;
        return parent[node] = findPar(parent[node]);
    }
    void unionByRank(int u, int v)
    {
        int ulp_u = findPar(u);
        int ulp_v = findPar(v);

        if (ulp_u == ulp_v)
            return;

        if (rank[ulp_u] > rank[ulp_v])
            parent[ulp_v] = ulp_u;
        else if (rank[ulp_u] < rank[ulp_v])
            parent[ulp_u] = ulp_v;
        else
        {
            parent[ulp_u] = ulp_v;
            rank[ulp_u]++;
        }
    }
    void unionBySize(int u, int v)
    {
        int ulp_u = findPar(u);
        int ulp_v = findPar(v);

        if (ulp_u == ulp_v)
            return;
        if (size[ulp_u]<size[ulp_v]){
            
            // history.push({ulp_u, parent[ulp_u]});
            // history.push({ulp_v, size[ulp_v]});
            
            parent[ulp_u]=ulp_v;
            size[ulp_v]+=size[ulp_u];
        }
        else{
            
            // history.push({ulp_v, parent[ulp_v]});
            // history.push({ulp_u, size[ulp_u]});
            
            parent[ulp_v]=ulp_u;
            size[ulp_u]+=size[ulp_v];
        }
        

    }

    // void rollback() {
    //     if (history.empty()) return;  // No operations to undo

    //     // Restore size of the larger component
    //     auto [node1, originalSize] = history.top();
    //     history.pop();
    //     size[node1] = originalSize;

    //     // Restore parent of the smaller component
    //     auto [node2, originalParent] = history.top();
    //     history.pop();
    //     parent[node2] = originalParent;
    // }
};
