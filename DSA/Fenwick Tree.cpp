//Q Block Placement Queries
class FenwickTree{
    public:
    vector<int>bit;
    int n;
    FenwickTree(int n){
        this->n=n;
        bit= vector<int>(n+1,0);
    }
    void add(int id,int val){
        while(id<=n){
            bit[id]=max(bit[id],val);
            id+=(id&-id);
        }
    }
    int query(int id){
        int maxAns=0;
        while(id>0){
            maxAns=max(maxAns,bit[id]);
            id-=(id&-id);
        }
        return maxAns;
    }
};
