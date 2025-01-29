typedef long long ll;
class Solution {
public:
    ll mod=1000000000+7;
    ll hashValue(string s,int radix,int m){
        ll ans=0;
        ll factor=1;
        for(int i=m-1;i>=0;i--){
            ans+=((s[i]-'a')*factor)%mod;
            factor=(factor*radix)%mod;
        }

        return ans%mod;
    }
    int strStr(string haystack, string needle) {
        int n=haystack.length();
        int m=needle.length();
        if(n<m)return -1;
        ll radix=26;
        ll max_weight=1;
        
        for(int i=1;i<=m;i++){
            max_weight=(max_weight*radix)%mod;//(26)^m
        }

        // hash search pattern
        ll hashNeedle=hashValue(needle,radix,m),hashHay=0;
        //apply sliding window now on string haystack
        for(ll i=0;i<=n-m;i++){
            if(i==0){
                hashHay=hashValue(haystack.substr(0,m),radix,m);
            }
            else{
                hashHay=((hashHay*radix)%mod-((haystack[i-1]-'a')*max_weight)%mod+
                (haystack[i+m-1]-'a')+mod)%mod;
            }
            if(hashHay==hashNeedle){
                //compare both strings for spurious hits or hashcollisions
                for(ll j=0;j<m;j++){
                    if(needle[j]!= haystack[j+i])break;
                    if(j==m-1)return i;
                }
            }
        }
        return -1;
    }
};
