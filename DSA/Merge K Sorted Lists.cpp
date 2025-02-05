/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode() : val(0), next(nullptr) {}
 *     ListNode(int x) : val(x), next(nullptr) {}
 *     ListNode(int x, ListNode *next) : val(x), next(next) {}
 * };
 */
class Solution {
public:
    ListNode* mergeKLists(vector<ListNode*>& lists) {
        ListNode*dNode=new ListNode(-1);
        ListNode* temp=dNode;
        priority_queue<pair<int,ListNode*>,vector<pair<int,ListNode*>>
        ,greater<pair<int,ListNode*>>>pq;
        int size=lists.size();
        //pq is ready with head of all k linked lists in sorted fashion
        for(int i=0;i<size;i++){
            if(lists[i]!=NULL){
                pq.push({lists[i]->val,lists[i]});//{val,Node}
            }
        }
        while(!pq.empty()){
            auto it=pq.top();
            pq.pop();
            ListNode* currNode=it.second;
            temp->next=currNode;
            if(currNode->next){
                pq.push({currNode->next->val,currNode->next});
                //add next node if exists into the pq
            }
            temp=temp->next;
        }
        return dNode->next;
    }
};
