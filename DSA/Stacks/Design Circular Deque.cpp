class MyCircularDeque {
public:
    vector<int>arr;
    int front,rear;
    int size=0,K=0;
    MyCircularDeque(int k) {
        arr=vector<int>(k,0);
        K=k;
        front=0;
        rear=K-1;
    }
    
    bool insertFront(int value) {
        if(isFull())return false;
        front=(front-1+K)%K;
        arr[front]=value;//index->1=3
        size++;
        return true;
    }
    
    bool insertLast(int value) {
        if(isFull())return false;
        rear=(rear+1)%K;
        arr[rear]=value;//index->0=1,index->1=2
        size++;
        return true;
    }
    
    bool deleteFront() {
        if(isEmpty())return false;
        front=(front+1)%K;
        size--;
        return true;
    }
    
    bool deleteLast() {
        if(isEmpty())return false;
        rear=(rear-1+K)%K;
        size--;
        return true;
    }
    
    int getFront() {
        if(isEmpty())return -1;
        return arr[front];
    }
    
    int getRear() {
        if(isEmpty())return -1;
        return arr[rear];
    }
    
    bool isEmpty() {
        return size==0;
    }
    
    bool isFull() {
        return (size==K);
    }
};

/**
 * Your MyCircularDeque object will be instantiated and called as such:
 * MyCircularDeque* obj = new MyCircularDeque(k);
 * bool param_1 = obj->insertFront(value);
 * bool param_2 = obj->insertLast(value);
 * bool param_3 = obj->deleteFront();
 * bool param_4 = obj->deleteLast();
 * int param_5 = obj->getFront();
 * int param_6 = obj->getRear();
 * bool param_7 = obj->isEmpty();
 * bool param_8 = obj->isFull();
 */
