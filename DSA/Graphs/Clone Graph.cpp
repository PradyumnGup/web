/*
// Definition for a Node.
class Node {
public:
    int val;
    vector<Node*> neighbors;
    Node() {
        val = 0;
        neighbors = vector<Node*>();
    }
    Node(int _val) {
        val = _val;
        neighbors = vector<Node*>();
    }
    Node(int _val, vector<Node*> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
};
*/

class Solution {
public:
    void dfs(Node* node,Node* clone_node,unordered_map<Node*,Node*>&mp){
        for(Node* adjNode:node->neighbors){
            if(mp.find(adjNode)==mp.end()){
                Node* clone_neighbor=new Node(adjNode->val);
                mp[adjNode]=clone_neighbor;
                clone_node->neighbors.push_back(clone_neighbor);
                dfs(adjNode,clone_neighbor,mp);
            }
            else{
                clone_node->neighbors.push_back(mp[adjNode]);
            }
        }
    }
    Node* cloneGraph(Node* node) {
        if(node==NULL)return NULL;
        Node* clone_node=new Node(node->val);
        unordered_map<Node*,Node*>mp;
        mp[node]=clone_node;
        dfs(node,clone_node,mp);
        return clone_node;
    }
};
