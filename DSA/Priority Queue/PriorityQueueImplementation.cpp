// Online C++ compiler to run C++ program online
#include <iostream>
#include <bits/stdc++.h>
using namespace std;

class PriorityQueue{
    public:
    vector<int>helperArray;
    
    PriorityQueue(){
        helperArray.resize(1,-1);
    }   
    
    void push(int value){
        helperArray.push_back(value);
        int curr_index=helperArray.size()-1;
        while(curr_index>1){
            int parent=(curr_index)/2;
            if(helperArray[parent]>helperArray[curr_index]){
                break;
            }
            else{
                swap(helperArray[parent],helperArray[curr_index]);
            }
            curr_index=parent;
        }
    }
    
    void pop(){
        helperArray[1]=helperArray[helperArray.size()-1];
        helperArray.pop_back();
        //now heapify down
        int index=1;
        
        
        while(index<helperArray.size()){
            int largest = index;
            int leftChild=2*index;
            int rightChild=2*index+1;
            if(leftChild<helperArray.size() && helperArray[leftChild]>helperArray[largest]){
                largest=leftChild;
            }
            if(rightChild<helperArray.size() && helperArray[rightChild]>helperArray[largest]){
                largest=rightChild;
                
            }
            if (largest == index)
                break;
            
            swap(helperArray[largest],helperArray[index]);
            index=largest;
        }
    }
    
    bool isEmpty(){
        return (helperArray.size()==1);
    }
    
    int top(){
        return helperArray[1];
    }
    
    int size() {
        return helperArray.size() - 1;
    }
};
int main() {
    PriorityQueue pq;
    pq.push(2);
    pq.push(3);
    pq.push(4);
    cout<<pq.size()<<endl;
    while(!pq.isEmpty()){
        int val=pq.top();
        cout<<val<<endl;
        pq.pop();
        cout<<pq.size()<<endl;
    }
    
    return 0;
}
