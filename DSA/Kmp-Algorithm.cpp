class Solution {

   
    public:
        vector<int> computeLPS(string s,vector<int>&LPS){
                int n=s.size();
                
                LPS[0]=0;
                int i=1,len=0;
                while(i<n){
                    if(s[i]==s[len]){
                        len++;
                        LPS[i]=len;
                        i++;
                    }
                    else{
                        if(len!=0){
                            len=LPS[len-1];
                            
                        }
                        else{
                            LPS[i]=0;
                            i++;
                        }
                    }
                }
                return LPS;
        }

        vector <int> search(string pat, string txt)
        {
            int m=pat.size(),n=txt.size();
           vector<int>lps(m,0);
           computeLPS(pat,lps);
          
            //KMP algo
            
            int i=0,j=0;
            vector<int>ans;
            while(i<n){
                if(pat[j]==txt[i]){
                    i++;
                    j++;
                }
                if(j==m){
                    ans.push_back(i-j);
                    j=lps[j-1];
                }
                else if(txt[i]!=pat[j]){
                    if(j!=0){
                        j=lps[j-1];
                    }
                    else{
                        i++;
                    }
                }
            }
            // if(ans.size()==0)ans.push_back(-1);
            return ans;
        }
     

};
