/*
struct Job
{
    int id;	 // Job Id
    int deadline; // Deadline of job
    int profit; // Profit if job is over before or on deadline
};
*/

class Solution {
  public:
    // Function to find the maximum profit and the number of jobs done.
     bool static comp(Job a,Job b){
        return a.profit>b.profit;
    }
    vector<int> JobScheduling(Job jobs[], int n) {
        
        sort(jobs,jobs+n,comp);
        
        int maxDeadline=-1;
        for(int i=0;i<n;i++){
            maxDeadline=max(maxDeadline,jobs[i].deadline);
        }
        vector<int>hash(maxDeadline+1,0);
        int sum=0,cnt=0;
        for(int i=0;i<n;i++){
            for(int j=jobs[i].deadline;j>=1;j--){
                if(!hash[j]){
                    sum+=jobs[i].profit;
                    cnt++;
                    hash[j]=jobs[j].id;
                    break;
                }
            }
            
        }
        return {cnt,sum};
    }
};
