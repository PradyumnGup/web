/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode(int x) : val(x), left(NULL), right(NULL) {}
 * };
 */
class Solution {
public:
    TreeNode* lca(TreeNode* node,TreeNode* p, TreeNode* q){
        if(node==NULL)return NULL;

        if(node==p)return p;
        if(node==q)return q;
        
        TreeNode* lh=lca(node->left,p,q);//5
        TreeNode* rh=lca(node->right,p,q);//X
        //split point which is lca
        if(lh==p && rh==q)return node;
        if(lh==q && rh==p)return node;

        //
        if(lh==NULL)return rh;
        if(rh==NULL)return lh;
        return node;

    }
    TreeNode* lowestCommonAncestor(TreeNode* root, TreeNode* p, TreeNode* q) {
        return lca(root,p,q);
    }
};
