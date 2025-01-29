class Solution {
public:
    int countPrimes(int n) {
        //sieve of eratosthenes optimal solution
      //Space ->O(N)
        bool *isPrime = new bool[n];
      // Time -> O(N)
        for(int i=2;i<n;i++){
            isPrime[i]=true;
        }
        //O(Nlog(logN)) ->pime harmonic series
        for(int i=2;i*i<n;i++){
            if(!isPrime[i]){
                continue;
            }
            for(int j=i*i;j<n;j+=i){
                isPrime[j]=false;
            }
        }
        int countOfPrimes=0;
        //O(N) for counting or printing primes.
        for(int i=2;i<n;i++){
            if(isPrime[i]){
                countOfPrimes++;
            }
        }
        return countOfPrimes;
    }
};
