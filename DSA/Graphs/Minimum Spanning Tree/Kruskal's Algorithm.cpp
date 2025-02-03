


class DisjointSet
{
    vector<int>parent,rank,size;
        
public:
        DisjointSet(int N){
          parent.resize(N+1);
          rank.resize(N+1,0);
          size.resize(N+1,1);
          for(int i=0;i<=N;i++){        
              parent[i]=i;
          }
        }
        
        int findpar(int node){
            if(node==parent[node])return node;
            return parent[node]=findpar(parent[node]);
        }
        
        void unionbyrank(int u,int v){
            int pu=findpar(u);
            int pv=findpar(v);
            
            if(rank[pu]<rank[pv]){
                parent[pu]=pv;
            }
            else if(rank[pu]>rank[pv]){
                parent[pv]=pu;
            }
            else{
                parent[pv]=pu;
                rank[pu]++;
            }
        }
        
          void unionbysize(int u,int v){
            int pu=findpar(u);
            int pv=findpar(v);
            
            if(size[pu]<size[pv]){
                parent[pu]=pv;
                size[pv]+=size[pu];
            }
            else {
                parent[pv]=pu;
                size[pu]+=size[pv];
            }
            
        }
};
class Solution
{
	public:
	//Function to find sum of weights of edges of the Minimum Spanning Tree.
    int spanningTree(int V, vector<vector<int>> adj[])
    { 
        vector<pair<int,pair<int,int>>> edges;
        for(int i=0;i<V;i++){
            for(auto it:adj[i]){
                int adjNode=it[0];
                int wt=it[1];
                int node=i;
                edges.push_back({wt,{node,adjNode}});
            }
        }
        DisjointSet ds(V);
        sort(edges.begin(),edges.end());
        int mstWt=0;
        for(auto it:edges){
             int adjNode=it.second.second;
             int wt=it.first;
             int node=it.second.first;
             if(ds.findpar(node)!=ds.findpar(adjNode)){
                 mstWt+=wt;
                 ds.unionbysize(node,adjNode);
             }
        }
        return mstWt;
    }
};
