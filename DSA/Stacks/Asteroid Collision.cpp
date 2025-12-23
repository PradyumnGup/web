class Solution {
public:
    vector<int> asteroidCollision(vector<int>& asteroids) {
        int n=asteroids.size();
        vector<int> answer;
        stack<int>st;
        for(int i=0;i<n;i++){
            if(asteroids[i]>0){
                st.push(asteroids[i]);
            }
            else{
                while(!st.empty() && st.top()>0 && st.top()<abs(asteroids[i])){
                    st.pop();
                }
                if(!st.empty() && st.top()==abs(asteroids[i])){
                    st.pop();
                }
                else if(st.empty() || st.top()<0){
                    st.push(asteroids[i]);
                }
            }
        }
        int newSize=st.size();
        answer=vector<int>(newSize,0);
        int index=0;
        while(!st.empty()){
            answer[newSize-1-index++]=st.top();
            st.pop();
        }
        return answer;
    }
};
//positive values push krenge stack mei
