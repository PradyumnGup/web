class Solution {
public:
    string longestPalindrome(string s) {
        // int n=s.length();
        // int maxLen=0;
        // string res="";
        // //axis method
        // //odd length
        // for(int ax=0;ax<n;ax++){
        //     int len=1;
        //     int orb=1;
        //     while(ax-orb>=0 && ax+orb<n && s[ax-orb]==s[ax+orb]){
        //         orb++;
        //         len+=2;
        //     }
        //     if(maxLen<len){
        //         maxLen=len;
        //         int si=ax-len/2;
        //         res=s.substr(si,len);
        //     }
        // }
        

        // //even length
        //  for(int ax=0;ax<n-1;ax++){
        //     int len=0;
        //     int orb=1;
        //     while(ax-orb+1>=0 && ax+orb<n && s[ax-orb+1]==s[ax+orb]){
        //         orb++;
        //         len+=2;
        //     }
        //     if(maxLen<len){
        //         int si=ax-len/2+1;
        //         maxLen=len;

        //         res=s.substr(si,len);
        //     }
        // }


        // return res;

        int n=s.length();
        string res="";
        int index=0;
        vector<vector<int>>dp(n,vector<int>(n,0));
        int len=0;
        for(int g=0;g<n;g++){
            for(int i=0,j=g;j<n;i++,j++){
                if(g==0){
                    dp[i][j]=true;
                }
                else if(g==1){
                    if(s[i]==s[j]){
                        dp[i][j]=1;
                    }
                    else{
                        dp[i][j]=0;
                    }
                }
                else{
                    if(s[i]==s[j] && dp[i+1][j-1]==1){
                        dp[i][j]=1;
                    }
                    else{
                        dp[i][j]=0;
                    }
                }
                if(dp[i][j]==1){
                    index=i;
                    len=g+1;
                }
            }
        }
        return s.substr(index,len);
    }
};
