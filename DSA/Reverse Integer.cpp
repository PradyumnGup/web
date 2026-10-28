class Solution {
public:
    int reverse(int x) {
        int res=0;
        int max=INT_MAX;
        int min=INT_MIN;
        while(x){
            int digit=x%10;
            x=x/10;
            if(res>max/10 || (res==max/10 && digit>=max%10))return 0;
            if(res<min/10 || (res==min/10 && digit<=min%10))return 0;
            res=res*10+digit;
            
        }
        return res;
    }
};
