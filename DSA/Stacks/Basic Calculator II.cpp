class Solution {
public:
    int calculate(string s) {
        int len=s.length();
        stack<int>stack;
        char operation='+';
        long long number = 0;
        for(int i=0;i<len;i++){
            char currChar=s[i];
            if(isdigit(currChar)){
                number=number*10+currChar-'0';
            }
            if(!isdigit(currChar) && !iswspace(currChar) || i==len-1){
                if(operation=='+'){
                    stack.push((int)number);
                }
                else if(operation=='-'){
                    stack.push(-(int)number);
                }
                else if(operation=='*'){
                    int val=stack.top();
                    stack.pop();
                    stack.push(val*number);
                }
                else if(operation=='/'){
                    int val=stack.top();
                    stack.pop();
                    stack.push(val/number);
                }
                operation=currChar;
                number=0;
            }
            
        }
        int ans=0;
        while(!stack.empty()){
            int val=stack.top();
            stack.pop();
            ans+=val;
        }
        return ans;
    }
};
