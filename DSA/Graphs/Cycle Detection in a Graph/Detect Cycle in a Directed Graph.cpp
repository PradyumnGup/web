class Solution {
  public:
    // Function to detect cycle in a directed graph.
    bool dfs(int node,vector<int> adj[],vector<int>&vis,vector<int>&pathVis){
        vis[node]=1;
        pathVis[node]=1;
        for(auto adjNode:adj[node]){
            if(!vis[adjNode]){
                if(dfs(adjNode,adj,vis,pathVis)){
                    return true;
                }
            }
            else if(pathVis[adjNode]){
                return true;
            }
        }
        pathVis[node]=0;
        return false;
    }
    bool isCyclic(int V, vector<int> adj[]) {
        vector<int>vis(V,0);
        vector<int>pathVis(V,0);
        for(int i=0;i<V;i++){
            if(!vis[i]){
                if(dfs(i,adj,vis,pathVis))return true;
            }
        }
        return false;
    }
};
