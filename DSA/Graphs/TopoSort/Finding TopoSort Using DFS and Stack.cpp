class Solution
{
	public:
	void dfs(int node,int V,vector<int> adj[],int vis[],stack<int>&st){
	    vis[node]=1;
	    for(auto it:adj[node]){
	        if(!vis[it]){
	            dfs(it,V,adj,vis,st);
	        }
	        
	    }
	    st.push(node);
	}
	//Function to return list containing vertices in Topological order. 
	vector<int> topoSort(int V, vector<int> adj[]) 
	{
	    // code here
	    vector<int>toposor;
	    stack<int> st;
	    int vis[V]={0};
	    for(int i=0;i<V;i++){
	        if(!vis[i]){
	            dfs(i,V,adj,vis,st);
	        }
	    }
	    while(!st.empty()){
	        toposor.push_back(st.top());
	        st.pop();
	    }
	    return toposor;
	}
};
