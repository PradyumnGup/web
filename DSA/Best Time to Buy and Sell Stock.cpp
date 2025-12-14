class Solution {
public:
    int maxProfit(vector<int>& prices) {
        int n=prices.size();
        int minBeforeSell=prices[0];
        int maxProfit=0;
        for(int i=1;i<n;i++){
            if(minBeforeSell<prices[i]){
                maxProfit=max(maxProfit,prices[i]-minBeforeSell);
            }
            minBeforeSell=min(minBeforeSell,prices[i]);
        }
        return maxProfit;
    }
};
