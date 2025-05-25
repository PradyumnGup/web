/*
struct Node {
    int data;
    Node *left;
    Node *right;

    Node(int val) {
        data = val;
        left = right = NULL;
    }
};
*/
class Solution {
  public:
    Node* middle(Node* node){
        Node* slow=node;
        Node* fast=node->right->right;
        while(fast && fast->right){
            slow=slow->right;
            fast=fast->right->right;
        }
        return slow;
    }
    Node* sortedListToBST(Node* head) {
        if(head==NULL)return NULL;
        if(head->right==NULL){
            return new Node(head->data);
        }
        Node* mid=middle(head);
        Node* trueMid=mid->right;
        mid->right=NULL;
        Node* root=new Node(trueMid->data);
        root->left=sortedListToBST(head);
        root->right=sortedListToBST(trueMid->right);
        return root;
    }
    Node* mergeTwoLists(Node* list1, Node* list2) {
          if(list1 == NULL)
            return list2;
		
		// if list2 happen to be NULL
		// we will simply return list1.
        if(list2 == NULL)
            return list1;

        Node*  l1=list1;
        Node* l2=list2;
        Node*  dummy=new Node(-1);
        Node*  temp=dummy;
        while(l1!=NULL && l2!=NULL){
            if(l1->data<l2->data){
                temp->right=l1;
                temp=l1;
                l1=l1->right;
            }
            else{
                temp->right=l2;
                temp=l2;
                l2=l2->right;
            }
        }
        while(l1!=NULL){
                temp->right=l1;
                temp=l1;
                l1=l1->right;
        }
        while(l2!=NULL){
                temp->right=l2;
                temp=l2;
                l2=l2->right;
        }
        return dummy->right;
    }
    Node *flattenBST(Node *root)
    {
        if(root==NULL)return NULL;
        
        Node* head=flattenBST(root->left);
        root->left=NULL;
        
        if(head!=NULL){
            Node* temp=head;
            while(temp && temp->right){
                temp=temp->right;
            }
            
            temp->right=root;
        }
        else{
            head=root;
        }

        root->right=flattenBST(root->right);
        return head;
    }
    vector<int> merge(Node *root1, Node *root2) {
        //1.Convert both bsts into sorted lists
        
        Node* list1=flattenBST(root1);
        Node* list2=flattenBST(root2);
        
        //2.Merge two sorted list into 1 sorted list
        Node* mergedHead=mergeTwoLists(list1,list2);
        //3.Convert sorted ll to bst
        Node* mergedBSTHead=sortedListToBST(mergedHead);
        //4. convert inorder
        vector<int>inorder;
        inorderTrav(mergedBSTHead,inorder);
        return inorder;
    }
    void inorderTrav(Node* root,vector<int>&inorder){
        if(root==NULL)return;
        inorderTrav(root->left,inorder);
        inorder.push_back(root->data);
        inorderTrav(root->right,inorder);
        return;
    }
};
