//User function Template for C++

class Merge{
  public: 
  int val;
    int row;
    int col;
    Merge(int val,int i,int j){
        this->val=val;
        row=i;
        col=j;
    }
};

struct compare{
   public:
   bool operator()(Merge *a, Merge *b){
       return a->val>b->val;
    }
};
class Solution
{
    public:
    //Function to merge k sorted arrays.
    vector<int> mergeKArrays(vector<vector<int>> arr, int K)
    {
        //code here
        priority_queue<Merge*,vector<Merge*>,compare> minh;
        vector<int> ans;
        //push first element here 1,4,7
        for(int i=0;i<K;i++){
            Merge *temp=new Merge(arr[i][0],i,0);
            minh.push(temp);
            
        }
        while(minh.size()>0){
            Merge *temp = minh.top();
            ans.push_back(temp->val);
            minh.pop();
            int i=temp->row;
            int j=temp->col;
            
            if(j+1<K){
                Merge *tmp=new Merge(arr[i][j+1],i,j+1);
                minh.push(tmp);
            }
        }
        return ans;
        //
    }
};
