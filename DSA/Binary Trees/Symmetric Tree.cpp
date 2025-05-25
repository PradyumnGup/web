/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */
class Solution {
public:
    bool isSym(TreeNode* p,TreeNode* q){
        if(!p || !q){
            return p==q;
        }
        if(p->val!=q->val){
            return false;
        }
        return isSym(p->right,q->left)&&isSym(p->left,q->right);
    }
    bool isSymmetric(TreeNode* root) {
        return isSym(root->left,root->right);
    }
};
