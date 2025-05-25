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
//preorder->root,left,right
//inorder ->left,root,right sorted in bst
//3,1,4,5,7
//1,3,4,5,7

//            3
//         1    4
//                 5
//                   7
//O(n^2) approach
class Solution {
public:
    TreeNode* bst(int preStart,int preEnd,vector<int>&preorder){
        if(preStart>preEnd)return NULL;
        int index=preStart;
        while(index<=preEnd){
            if(preorder[index]>preorder[preStart]){
                break;
            }
            index++;
        }
        
        TreeNode* root=new TreeNode(preorder[preStart]);
        root->left=bst(preStart+1,index-1,preorder);
        root->right=bst(index==preStart?index+1:index,preEnd,preorder);
        return root;
    }
    TreeNode* bstFromPreorder(vector<int>& preorder) {
        int n=preorder.size();
        return bst(0,n-1,preorder);
    }
};

//this is o(n).
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
