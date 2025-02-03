// User function Template for C++

class Solution {
  public:
    /*  Function to implement Bellman Ford
     *   edges: vector of vectors which represents the graph
     *   S: source vertex to start traversing graph with
     *   V: number of vertices
     */
    vector<int> bellman_ford(int v, vector<vector<int>>& edges, int src) {
        vector<int>dist(v,1e8);
        dist[src]=0;
        for(int i=0;i<v-1;i++){
            for(auto it:edges){
                int node=it[0];
                int adjNode=it[1];
                int edgeWt=it[2];
                if(dist[node]!=1e8 && dist[node]+edgeWt<dist[adjNode]){
                    dist[adjNode]=dist[node]+edgeWt;
                }
            }
        }
        //Nth relaxation to find negative cycle
         for(auto it:edges){
                int node=it[0];
                int adjNode=it[1];
                int edgeWt=it[2];
                if(dist[node]!=1e8 && dist[node]+edgeWt<dist[adjNode]){
                    return {-1};
                }
            }
        return dist;
    }
};
