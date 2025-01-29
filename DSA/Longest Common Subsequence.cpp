class Solution {
public:
    int lcs(int index1,int index2,string text1,string text2,vector<vector<int>>&dp){
        if(index2<0)return 0;
        if(index1<0)return 0;
        if(dp[index1][index2]!=-1)return dp[index1][index2];
        int take=INT_MIN,notTake=INT_MIN;
        if(text1[index1]==text2[index2]){
            take=1+lcs(index1-1,index2-1,text1,text2,dp);
        }
        else{
            notTake=max(lcs(index1,index2-1,text1,text2,dp),lcs(index1-1,index2,text1,text2,dp));
        }
        
        return dp[index1][index2]=max(take,notTake);
    }
    int longestCommonSubsequence(string text1, string text2) {
        int n=text1.size(),m=text2.size();
        // vector<vector<int>>dp(n,vector<int>(m,-1));
        vector<vector<int>>dp(n+1,vector<int>(m+1,0));
        for(int index1=1;index1<=n;index1++){
            for(int index2=1;index2<=m;index2++){
                int take=INT_MIN,notTake=INT_MIN;
                if(text1[index1-1]==text2[index2-1]){
                    take=1+dp[index1-1][index2-1];
                }
                else{
                    notTake=max(dp[index1][index2-1],dp[index1-1][index2]);
                }
                
                dp[index1][index2]=max(take,notTake);
            }
        }
        // int ans=lcs(n-1,m-1,text1,text2,dp);
        int ans=dp[n][m];
        return ans==INT_MIN?0:ans;
    }
};
