class Node{
    public: int val;
            int key;
            Node* next;
            Node* prev;
    public: Node(int key,int val){
        this->val=val;
        this->key=key;
        next=NULL;
        prev=NULL;
    }
};
class LRUCache {
private: Node* head;
         Node* tail;
         int cap=0;
         int maxSize=0;
         unordered_map<int,Node*>mp;
public:
    LRUCache(int capacity) {
        head=new Node(-1,-1);
        tail=new Node(-1,-1);
        head->next=tail;
        tail->prev=head;
        this->maxSize=capacity;
    }
private:
    void deleteNodeBeforeTail(Node* node){
        Node* prevNode=node->prev;
        Node* nextNode=node->next;
        prevNode->next=nextNode;
        nextNode->prev=prevNode;
    }
    void insertAfterHead(Node* node){
        Node* headNext=head->next;
        head->next=node;
        node->prev=head;
        node->next=headNext;
        headNext->prev=node;
    }
    public:
    int get(int key) {
        if(mp.find(key)!=mp.end()){
            Node* node=mp[key];
            //this is mru update its position after head
            deleteNodeBeforeTail(node);
            insertAfterHead(node);
            return node->val;
        }
        else{
            return -1;
        }
    }
    
    void put(int key, int value) {
        if(mp.find(key)!=mp.end()){
            //If already in map
            Node* node=mp[key];
            node->val=value;
            deleteNodeBeforeTail(node);
            insertAfterHead(node);
            // mp[key]=node;
            return ;
        }
        else{
            if(cap==maxSize){
                //evicting lru which is before tail node
                mp.erase(tail->prev->key);
                deleteNodeBeforeTail(tail->prev);
                cap--;
            }
            cap++;
            Node* newNode=new Node(key,value);
            mp[key]=newNode;
            //now this node is mru so insert after head
            insertAfterHead(newNode);
            return ;
        }
    }
};
// NULL<-head <->tail->NULL
/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache* obj = new LRUCache(capacity);
 * int param_1 = obj->get(key);
 * obj->put(key,value);
 */
