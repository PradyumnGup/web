#include <bits/stdc++.h> 
pair<int,int> farthestNode(int src,unordered_map<int,vector<int>>graph,int n,vector<int>&vis){
    
    vis[src]=1;
    int farthestNode=src;
    int maxi=0;
    queue<pair<int,int>>q;
    vector<int>dist(n,INT_MIN);
    q.push({0,src});
    dist[src]=0;
    while(!q.empty()){
        auto it=q.front();
        q.pop();
        int node=it.second;
        int dis=it.first;

        for(auto adjNode:graph[node]){
            if(!vis[adjNode] && 1+dis>dist[adjNode]){
                vis[adjNode]=1;
                dist[adjNode]=1+dis;
                q.push({dist[adjNode],adjNode});
                if(dist[adjNode]>maxi){
                    maxi=dist[adjNode];
                    farthestNode=adjNode;
                }
            }
        }
    }
    
    return make_pair(farthestNode,maxi);
}
int longestPath(vector<vector<int>> &edges, int n)
{
    //find farthest node  from any node
    //from that node find longest path and return it
    unordered_map<int,vector<int>>graph;
    for(auto edge:edges){
        int u=edge[0];
        int v=edge[1];
        graph[u].push_back(v);
        graph[v].push_back(u);
    }
    vector<int>vis(n,0);
    pair<int,int>p = farthestNode(0,graph,n,vis);
    
    vis.assign(n,0);
    pair<int,int>p2=farthestNode(p.first,graph,n,vis);
    return p2.second;
}

