class Solution {
  public:
  vector<int>spf;
  
    void sieve() {
        spf.resize(2*100000+1);
        for(int i=0;i<=2*100000;i++)spf[i]=i;

        for(int i=2;i*i<=2*100000;i++){
            if(spf[i]==i){
                for(int j=i*i;j<=2*100000;j+=i){
                    if(spf[j]==j){
                        spf[j]=i;
                    }
                }
            }
        }
    }
    
    vector<int> findPrimeFactors(int N) {

         
        vector<int>primeFacts;
        sieve();
        int num=N;
        while(num!=1){
            
            primeFacts.push_back(spf[num]);
            num/=spf[num];
            
        }
        return primeFacts;
        
        //TC:numloglognum + nlognum
    }
};
