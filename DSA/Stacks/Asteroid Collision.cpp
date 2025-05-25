class Solution {
public:
    vector<int> asteroidCollision(vector<int>& asteroids) {
        stack<int>st;
        int n=asteroids.size();
        for(int i=0;i<n;i++){
            while(!st.empty() && asteroids[i]<0 && st.top()>0){
                int sum=asteroids[i]+st.top();
                if(sum<0){
                    st.pop();
                }
                else if(sum>0){
                    asteroids[i]=0;
                }
                else{
                    st.pop();
                    asteroids[i]=0; 
                }
            }
            if(asteroids[i]!=0)st.push(asteroids[i]);
        }
        int x=st.size();
        vector<int>answer(x,0);
        int index=0;
        while(!st.empty()){
            answer[x-index-1]=st.top();
            st.pop();
            index++;
        }
        return answer;
    }
};
//positive values push krenge stack mei
