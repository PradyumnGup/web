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
 void inorder(TreeNode* root, int k,int &cnt,int &ans){
        if (root==NULL)return ;
        inorder(root->left,k,cnt,ans);
        cnt++;
        if(cnt==k)ans=root->val;
        // v.push_back(root->val);
        inorder(root->right,k,cnt,ans);
    }
    int kthSmallest(TreeNode* root, int k) {
        int cnt=0,ans=0;
        inorder(root,k,cnt,ans);
        return ans;
    }
};
