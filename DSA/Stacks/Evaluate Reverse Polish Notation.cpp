class Solution {
public:
    long long operate(long long firstVal,long long secondVal,string token){
        if(token=="+"){
            return firstVal+secondVal;
        }
        else if(token=="-"){
            return secondVal-firstVal;
        }
        else if(token=="*"){
            return firstVal*secondVal;
        }
        else {
            return secondVal/firstVal;
        }
    }
    int evalRPN(vector<string>& tokens) {
        stack<long long>myStack;
        for(auto token:tokens){
            if(token=="+" || token=="-" || token=="*" || token=="/"){
                long long firstVal=myStack.top();
                myStack.pop();
                long long secondVal=myStack.top();
                myStack.pop();
                long long result=operate(firstVal,secondVal,token);
                // cout<<result<<endl;
                myStack.push(result);
            }
            else{
                cout<<stoi(token)<<endl;
                myStack.push(stoi(token));
            }
        }
        return myStack.top();
    }
};
//stack mei 2 values denge previous
//jab koi token aaya
//i pop 2 values from stack
//go token operation and push result in the stack.
