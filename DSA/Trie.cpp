struct Node{
public: 
    Node* links[26];
    bool flag;

    bool containsKey(char ch){
        return links[ch-'a']!=NULL;
    }
    void putKey(char ch,Node* newNode){
        links[ch-'a']=newNode;
    }
    Node* getKey(char ch){
        return links[ch-'a'];
    }
    bool isEnd(){
        return flag;
    }
    void setEnd(){
        flag=true;
    }
};
class Trie {
private:Node* root;
public:
    Trie() {
        root=new Node();
    }
    
    void insert(string word) {
        Node* node=root;
        int n=word.length();
        for(int i=0;i<n;i++){
            char ch=word[i];
            if(!node->containsKey(ch)){
                node->putKey(ch,new Node());
            }
            node=node->getKey(ch);
        }
        node->setEnd();
    }
    
    bool search(string word) {
        Node* node=root;
        int n=word.length();
        for(int i=0;i<n;i++){
            char ch=word[i];
            if(!node->containsKey(ch)){
                return false;
            }
            node=node->getKey(ch);
        }
        return node->isEnd();
    }
    
    bool startsWith(string prefix) {
        Node* node=root;
        int n=prefix.length();
        for(int i=0;i<n;i++){
            char ch=prefix[i];
            if(!node->containsKey(ch)){
                return false;
            }
            node=node->getKey(ch);
        }
        return true;
    }
};
