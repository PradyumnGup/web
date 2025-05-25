class Solution {
public:
    int longestValidParentheses(string s) {
        int left=0,right=0;
        int n=s.size();
        int maxi=0;
        stack<int>st;
        st.push(-1);
        for(int i=0;i<n;i++){
            if(s[i]=='('){
                st.push(i);
            }
            else if(s[i]==')'){
                st.pop();
                if(st.empty()){
                    st.push(i);
                }
                else{
                    maxi=max(maxi,i-st.top());
                }
            }
        }
        return maxi;
    }
};
