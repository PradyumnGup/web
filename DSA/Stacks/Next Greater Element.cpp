class Solution {
  public:
    // Function to find the next greater element for each element of the array.
    vector<int> nextLargerElement(vector<int>& arr) {
        int size_arr=arr.size();
        vector<int>nextGreater(size_arr,0);
        stack<int>stack;
        
        for(int index=size_arr-1;index>=0;index--){
            while(!stack.empty() && arr[stack.top()]<=arr[index]){
                stack.pop();
            }
            if(stack.empty()){
                nextGreater[index]=-1;
            }
            else {
                nextGreater[index]=arr[stack.top()];
            }
            stack.push(index);
        }
        
        return nextGreater;
    }
};
