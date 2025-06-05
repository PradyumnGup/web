class Solution {
public:
    string reorganizeString(string s) {
        int n=s.length();
        vector<int>freqChar(26,0);
        int maxFreq=0;
        char maxFreqChar;
        
        for(int i=0;i<n;i++){
            char ch=s[i];
            freqChar[ch-'a']++;
            if(freqChar[ch-'a']>(n+1)/2){
                return "";
            }
            if(freqChar[ch-'a']>maxFreq){
                maxFreq=freqChar[ch-'a'];
                maxFreqChar=ch;
            }
        }
        cout<<"HI"<<endl;
        //place this max freq char alternatively
        string finalString(n,' ');
        int i=0;
        while(maxFreq--){
            freqChar[maxFreqChar-'a']--;
            finalString[i]=maxFreqChar;
            i+=2;
        }
        //now place remaining char in the remaining pos
        for(int index=0;index<26;index++){
            
            while(freqChar[index]--){
                if(i>=n){
                    i=1;
                }
                finalString[i]=char(index+'a');
                i+=2;
            }
        }
        return finalString;
    }
};
