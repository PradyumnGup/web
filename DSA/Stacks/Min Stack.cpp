class MinStack {
public:
    stack<long long>st;
    long long mini=INT_MAX;
    MinStack() {
        
    }
    
    void push(int val) {
        //we have to push 2*val-mini
        if(st.empty()){
            st.push(1LL*val);
            mini=1LL*val;
        }
        else{
            if(1LL*val<mini){
                st.push(2*1LL*val*1LL-mini);
                mini=1LL*val;
            }
            else{
                st.push(1LL*val);
            }
        }
    }
    
    void pop() {
        long long st_top=st.top();
        st.pop();
        if(st_top<mini){
            //this is modified value
            mini=2*mini-st_top;
        }
        
    }
    
    int top() {
        if(st.empty())return -1;
        long long st_top=st.top();
        
        if(st_top<mini){
            //this is modified value hence mini is the top value here.
            return mini;

        }
        else{
            return st_top;
        }
    }
    
    int getMin() {
        return mini;
    }
};

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack* obj = new MinStack();
 * obj->push(val);
 * obj->pop();
 * int param_3 = obj->top();
 * int param_4 = obj->getMin();
 */
