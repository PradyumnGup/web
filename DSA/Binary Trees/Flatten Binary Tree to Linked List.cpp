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
    // TreeNode* prev=NULL;
    void flatten(TreeNode* root) {
    
        //stack O(n) and O(n)
//         if (root == NULL) return;
//         stack<TreeNode*> s;
//         s.push(root);
//         while(!s.empty())
//         {
//            TreeNode* node=s.top();
//             s.pop();
//             if(node->right)
//                 s.push(node->right);
//             if(node->left)
//                 s.push(node->left);
//             if(!s.empty())
//                 node->right=s.top();
//             node->left=NULL;
            
//         }
        // O(n)and O(1) morris
        
        if (root == NULL) return;
        
            TreeNode* curr=root;
        TreeNode* prev=NULL;
        while(curr!=NULL){
            if(curr->left!=NULL){
                 prev=curr->left;
                while(prev->right!=NULL){
                    prev=prev->right;
                }
                prev->right=curr->right;
                curr->right=curr->left;
                curr->left = NULL;
            }
            curr=curr->right;
        }
        
        
        
        
        
        
        
        
        
        //recursive  
//         if(root==NULL)return;
        
//         flatten(root->right);
//         flatten(root->left);
        
//         root->right=prev;
//         root->left=NULL;
//         prev=root;
//         return;
    }
};
