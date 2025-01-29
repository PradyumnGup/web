int longestCommonSubsequence(string text1, string text2) {
        int n=text1.size();
        int m=text2.size();
        vector<vector<int>>dp(n+1,vector<int>(m+1,0));
        for(int i=0;i<=n;i++){
            dp[i][0]=0;
        }
        for(int i=0;i<=m;i++){
            dp[0][i]=0;
        }

        for(int i=1;i<=n;i++){
            for(int j=1;j<=m;j++){
                //matching case
                if(text1[i-1]==text2[j-1]){
                 dp[i][j]=1+dp[i-1][j-1]; 
               }
               else{
                //not matching case
                 dp[i][j]=max(dp[i][j-1],dp[i-1][j]);
                    }
                }
             }
        //Printing Code
        //Backtracking 
       int i=n,j=m;
        string s="";
        for(int i=0;i<dp[n][m];i++)s+="$";
        int index=dp[n][m]-1;
        while(i>0&& j>0){
            if(text1[i-1]==text2[j-1]){
                s[index]=text1[i-1];
                index--;
                i--;
                j--;
            }
            else if(dp[i-1][j]>dp[i][j-1]){
                i--;
            }
            else{
                j--;
            }
        }
        
        for(int i=0;i<s.size();i++){
            cout<<s[i]<<"";
        }
        return s.size();
    }
