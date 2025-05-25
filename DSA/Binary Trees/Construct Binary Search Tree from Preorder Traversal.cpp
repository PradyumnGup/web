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
    TreeNode* bstFromPreorder(vector<int>& preorder) {
        int i=0;
        return bstfrompreorder(preorder,i,INT_MAX);   
    }
    TreeNode* bstfrompreorder(vector<int>& preorder,int &i,int maxi){
        if(i==preorder.size() || preorder[i]>maxi) return NULL;
        
        TreeNode* root=new TreeNode(preorder[i]);
        i++;
        
        root->left=bstfrompreorder(preorder,i,root->val);
        
        
        root->right=bstfrompreorder(preorder,i,maxi);
        
        return root;
    }
};
