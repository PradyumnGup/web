class MedianFinder {
private: priority_queue<int>maxh;
         priority_queue<int,vector<int>,greater<int>>minh;
public:
    MedianFinder() {
        
    }
    
    void addNum(int num) {
        //maxh mei median value agar top pe hai to usse saare chote left pe hai
        //maxh pe saari top se choti value push krenge
        
        if(maxh.empty() || maxh.top()>num){
            maxh.push(num);
        }
        else{
            minh.push(num);
        }

        //maintain 1 more in maxheap or equal size as minh
        if(maxh.size()>minh.size()+1){
            minh.push(maxh.top());
            maxh.pop();
        }
        else if(maxh.size()<minh.size()){
            maxh.push(minh.top());
            minh.pop();
        }
    }
    
    double findMedian() {
        if(maxh.size()==minh.size()){
            //even nos
            double median=(double)(maxh.top()+minh.top())/2;
            return median;
        }
        else{
            //odd
            double median=maxh.top();
            return median;
        }
    }
};

/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder* obj = new MedianFinder();
 * obj->addNum(num);
 * double param_2 = obj->findMedian();
 */
