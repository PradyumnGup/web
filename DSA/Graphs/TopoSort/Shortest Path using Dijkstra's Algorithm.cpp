// User Function Template
class Solution {
  public:
    // Function to find the shortest distance of all the vertices
    // from the source vertex src.
    vector<int> dijkstra(vector<vector<pair<int, int>>> &adj, int src) {
        //djkstra never works for negative cycle and negative edge weights
        //TC:-(E+V)LogV
        //bfs traversal to determine all least distances
        int V=adj.size();
        // priority_queue<pair<int,int>,vector<pair<int,int>>,greater<pair<int,int>>>pq;
        // pq.push({0,src});
        queue<pair<int,int>>q;
        q.push({0,src});
        vector<int>dist(V,1e9);
        dist[src]=0;
        while(!q.empty()){
            // pair<int,int> p =pq.top();
            pair<int,int> p=q.front();
            int node=p.second;
            int wt=p.first;
            // pq.pop();
            q.pop();
            for(auto it:adj[node]){
                int adjNode=it.first;
                int adjWt=it.second;
                if(dist[node]+adjWt<dist[adjNode]){
                    dist[adjNode]=dist[node]+adjWt;
                    // pq.push({dist[adjNode],adjNode});
                    q.push({dist[adjNode],adjNode});
                    
                }
            }
        }
        //creating shortest path array using distance array
        // vector<int>shortPath(V,-1);
        // for(int i=0;i<V;i++){
        //     if(dist[i]!=1e9){
        //         shortPath[i]=dist[i];
        //     }
        // }
        // return shortPath;
        return dist;
    }
};
