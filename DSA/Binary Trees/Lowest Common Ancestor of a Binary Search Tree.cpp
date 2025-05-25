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

        if(p->val<node->val && q->val<node->val){
            return lca(node->left,p,q);
        }
        if(p->val>node->val && q->val>node->val){
            return lca(node->right,p,q);
        }
        return node;
    }
    TreeNode* lowestCommonAncestor(TreeNode* root, TreeNode* p, TreeNode* q) {
        return lca(root,p,q);
    }
};
