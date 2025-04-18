/*   Scroll below to see JAVA code also	*/
/*
	MY YOUTUBE VIDEO ON THIS Qn : https://www.youtube.com/watch?v=qU4DAnv3o7g
   	Company Tags   	  	    : Facebook (Variation)
    	GFG Link       		    : https://practice.geeksforgeeks.org/problems/range-minimum-query/1#

    	Below code is for "Min" Query. Simply replace min() with max() and it will become for "Max" Query
*/


/************************************************************ C++ ***************************************************************************/
//Using Segment Tree
//T.C : O(q*log(n))
//S.C : O(4*n)
// Function to build the segment tree
void buildSegmentTree(int i, int l, int r, int segmentTree[], int arr[]) {
    if (l == r) {
        segmentTree[i] = arr[l];
        return;
    }
    
    int mid = l + (r - l) / 2;
    buildSegmentTree(2 * i + 1, l, mid, segmentTree, arr);
    buildSegmentTree(2 * i + 2, mid + 1, r, segmentTree, arr);
    segmentTree[i] = min(segmentTree[2 * i + 1], segmentTree[2 * i + 2]);
}

// Function to construct the segment tree
int* constructST(int arr[], int n) {
    int* segmentTree = new int[4 * n];
    buildSegmentTree(0, 0, n - 1, segmentTree, arr);
    return segmentTree;
}

// Function to query the segment tree for minimum value in range [start, end]
int querySegmentTree(int start, int end, int i, int l, int r, int segmentTree[]) {
    if (l > end || r < start) {
        return INT_MAX;
    }
    
    if (l >= start && r <= end) {
        return segmentTree[i];
    }
    
    int mid = l + (r - l) / 2;
    return min(querySegmentTree(start, end, 2 * i + 1, l, mid, segmentTree),
               querySegmentTree(start, end, 2 * i + 2, mid + 1, r, segmentTree));
}

// Function to return the minimum element in the range from a to b
int RMQ(int st[], int n, int a, int b) {
    return querySegmentTree(a, b, 0, 0, n - 1, st);
}

//point update in segtree
void updateSegTree(int index,int val,int i,int l,int r){
	if(l==r){
		segmentTree[i]=val;
		return;
	}
	int mid=l+(r-l)/2;
	if(index<=mid){
		updateSegTree(index,val,2*i+1,l,mid);
	}
	else{
		updateSegTree(index,val,2*i+2,mid+1,r);
	}
	segmentTree[i]=min(sementTree[2*i+1],sementTree[2*i+2]);
}


