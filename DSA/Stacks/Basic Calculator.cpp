class Solution {
public:
    int calculate(string s) {
        int n=s.length();
        int sign=+1;
        int number=0;
        int result=0;
        stack<int>stack;
        for(int i=0;i<n;i++){
            if(isdigit(s[i])){
                number=(number)*10+(s[i]-'0');
            }
            else if(s[i]=='+'){
                result+=sign*number;
                number=0;
                sign=+1;
            }
            else if(s[i]=='-'){
                result+=sign*number;
                number=0;
                sign=-1;
            }
            else if(s[i]=='('){
                stack.push(result);
                stack.push(sign);
                number=0;
                result=0;
                sign=+1;
            }
            else if(s[i]==')'){
                result+=(number*sign);
                number=0;

                //now pop stack and create final result till here
                int stack_sign=stack.top();
                stack.pop();
                int stack_result=stack.top();
                stack.pop();
                result*=stack_sign;
                result+=stack_result;
            }
            // else if(s[i]==' '){
            //     continue;
            // }
        }
        return result+(sign)*number;
    }
};
