class Node{
    public:int val;
            int key;
            Node* next;
            
    public: Node(int key,int val){
        this->val=val;
        this->key=key;
        this->next=NULL;
    }
};

class MyHashMap {
public:
vector<Node*>hash{100,NULL};
    MyHashMap() {
        
    }
    
    void put(int key, int value) {
        Node* head=hash[key%100];
        if(head==NULL){
            hash[key%100]=new Node(key,value);
            return ;
        }
        Node* temp=head;
        while(temp->next!=NULL){
            if(temp->key==key){
                temp->val=value;
                return ;
            }
            temp=temp->next;
        }
        if(temp->key==key){
            temp->val=value;
            return ;
        }
        temp->next=new Node(key,value);
        return;
    }
    
    int get(int key) {
        if(hash[key%100]==NULL)return -1;
        Node* head=hash[key%100];
        
        while(head!=NULL){
            if(head->key==key){
                return head->val;
            }
            head=head->next;
        }
        return -1;
    }
    
    void remove(int key) {
        if(hash[key%100]==NULL)return ;
        Node* head=hash[key%100];
        Node* prev=NULL;
        while(head!=NULL){
            if(head->key==key){
                //erase krdo
                if(prev==NULL){
                    Node* nxt=head->next;  
                    hash[key%100]=nxt;
                    return; 
                }
                Node* nxt=head->next;
                prev->next=nxt;
                return ;
            }
            prev=head;
            head=head->next;
        }
        return ;
    }
};

/**
 * Your MyHashMap object will be instantiated and called as such:
 * MyHashMap* obj = new MyHashMap();
 * obj->put(key,value);
 * int param_2 = obj->get(key);
 * obj->remove(key);
 */
