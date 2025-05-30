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
    TreeNode* buildTree(vector<int>& inorder, vector<int>& postorder) {
          map<int,int> mp;
        for(int i=0;i<postorder.size();i++){
            mp[inorder[i]]=i;
        }
        TreeNode* root = construct(postorder,0,postorder.size()-1,inorder,0,inorder.size()-1,mp);
        return root;
    }
        TreeNode* construct(vector<int>&postorder, int postStart, int postEnd, vector<int> &inorder,int inStart, int inEnd, map<int,int> &mp){
        if(postStart>postEnd || inStart>inEnd) return NULL;
        TreeNode* root = new TreeNode(postorder[postEnd]);
        int inRoot = mp[root->val];
        int numsLeft = inRoot-inStart;
        
        root->left = construct(postorder,postStart,postStart+numsLeft-1,inorder,inStart,inRoot-1,mp);
        root->right = construct(postorder,postStart+numsLeft,postEnd-1,inorder,inRoot+1,inEnd,mp);
        return root;
    
    }
};

//left root right
//[9 3 15 20 7]
//[9 15 7 20 3]
//left right root
