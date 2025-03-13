class Solution {
  public:
    // Function to sort an array using Heap Sort.
    void heapify(vector<int>&arr, int n, int index)  
    {
        int largest=index;
        int leftChild=2*largest+1;
        int rightChild=2*largest+2;
        if(leftChild<n && arr[largest]<arr[leftChild]){
            largest=leftChild;
        }
        if(rightChild<n && arr[largest]<arr[rightChild]){
            largest=rightChild;
        }
        if(largest!=index){
            swap(arr[largest],arr[index]);
            heapify(arr,n,largest);
        }
    }
   
    void heapSort(vector<int>& arr) {
        int n=arr.size();
        
    	
        //iterate over non leaf nodes
        for(int i=n/2-1;i>=0;i--){
            heapify(arr,n,i);
        }
        
         for(int i=n-1;i>0;i--)
        {
            swap(arr[0],arr[i]);
            heapify(arr,i,0);
        }
    }
};
