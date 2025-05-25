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
class BSTIterator{
private:stack<TreeNode*>st;
        bool isNext=true;
private: 
    void pushAll(TreeNode* node,bool isNext){
        if(isNext)for(;node!=NULL;node=node->left)st.push(node);
        else for(;node!=NULL;node=node->right)st.push(node);
    }
public:
    BSTIterator(TreeNode* node,bool next){
        isNext=next;
        pushAll(node,isNext);
        
    }
    bool hasNext(){
        return !st.empty();
    }
    //start from root node
    TreeNode* next(){
        TreeNode* node=st.top();
        st.pop();
        if(isNext)pushAll(node->right,isNext);
        else pushAll(node->left,isNext);
        return node;
    }
    // TreeNode* before(){
    //     TreeNode* node=st.top();
    //     st.pop();
    //     pushAll(node->right);
    //     return node;
    // }
};
class Solution {
public:
    bool findTarget(TreeNode* root, int k) {
        BSTIterator biNext(root,true);
        BSTIterator biBefore(root,false);

        int low=biNext.next()->val;
        int high=biBefore.next()->val;
        while(low<high){
            if(low+high==k)return true;
            else if(low+high<k){
                low=biNext.next()->val;
            }
            else{
                high=biBefore.next()->val;
            }
        }
        return false;
    }
};
//left root right
