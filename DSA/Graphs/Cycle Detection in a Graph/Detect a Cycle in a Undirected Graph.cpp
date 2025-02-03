class Solution {
 
    // Function to detect cycle in an undirected graph.
    private:
    bool detect(int src,vector<int>adj[],int vis[],int parent){
        vis[src]=1;
        queue<pair<int,int>>q;
        q.push({src,parent});
        while(!q.empty()){
            int node=q.front().first;
            int parent=q.front().second;
            q.pop();
            
            for(auto adjNode:adj[node]){
                if(!vis[adjNode]){
                    vis[adjNode]=1;
                    q.push({adjNode,node});
                }
                else if(parent!=adjNode){
                    return true;
                }
            }
        }
        return false;
    }
 public:
    bool isCycle(int V, vector<int> adj[]) {
        int vis[V]={0};
        for(int i=0;i<V;i++){
            if(!vis[i]){
                vis[i]=1;
                if(detect(i,adj,vis,-1))return true;
            }
        }
         return false;
    }
};
