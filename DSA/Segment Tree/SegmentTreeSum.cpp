#include <bits/stdc++.h>
using namespace std;

class SegmentTreeSum {
public:
    int *segmentTree;
    int *lazyTree;
    int n;

    SegmentTreeSum(int arr[], int size) {
        n = size;
        segmentTree = new int[4 * n];
        lazyTree = new int[4 * n];
        memset(lazyTree, 0, sizeof(int) * 4 * n);

        buildSegmentTree(0, 0, n - 1, arr);
    }

    void buildSegmentTree(int i, int l, int r, int arr[]) {
        if (l == r) {
            segmentTree[i] = arr[l];
            return;
        }

        int mid = l + (r - l) / 2;
        buildSegmentTree(2 * i + 1, l, mid, arr);
        buildSegmentTree(2 * i + 2, mid + 1, r, arr);

        segmentTree[i] = segmentTree[2 * i + 1] + segmentTree[2 * i + 2];
    }

    int querySegmentTree(int start, int end, int i, int l, int r) {
        applyLazy(i, l, r);

        if (l > end || r < start)
            return 0;

        if (l >= start && r <= end)
            return segmentTree[i];

        int mid = l + (r - l) / 2;

        return querySegmentTree(start, end, 2 * i + 1, l, mid) +
               querySegmentTree(start, end, 2 * i + 2, mid + 1, r);
    }

    int rangeSum(int a, int b) {
        return querySegmentTree(a, b, 0, 0, n - 1);
    }

    void updateSegTree(int index, int val, int i, int l, int r) {
        applyLazy(i, l, r);

        if (l == r) {
            segmentTree[i] = val;
            return;
        }

        int mid = l + (r - l) / 2;

        if (index <= mid)
            updateSegTree(index, val, 2 * i + 1, l, mid);
        else
            updateSegTree(index, val, 2 * i + 2, mid + 1, r);

        segmentTree[i] = segmentTree[2 * i + 1] + segmentTree[2 * i + 2];
    }

    void update(int index, int val) {
        updateSegTree(index, val, 0, 0, n - 1);
    }

    void applyLazy(int i, int l, int r) {
        if (lazyTree[i] != 0) {
            segmentTree[i] += (r - l + 1) * lazyTree[i];

            if (l != r) {
                lazyTree[2 * i + 1] += lazyTree[i];
                lazyTree[2 * i + 2] += lazyTree[i];
            }
            lazyTree[i] = 0;
        }
    }

    void updateRange(int start, int end, int i, int l, int r, int val) {
        applyLazy(i, l, r);

        if (l > end || r < start || l > r)
            return;

        if (l >= start && r <= end) {
            lazyTree[i] += val;
            applyLazy(i, l, r);
            return;
        }

        int mid = (l + r) / 2;
        updateRange(start, end, 2 * i + 1, l, mid, val);
        updateRange(start, end, 2 * i + 2, mid + 1, r, val);

        segmentTree[i] = segmentTree[2 * i + 1] + segmentTree[2 * i + 2];
    }

    void updateRange(int l, int r, int val) {
        updateRange(l, r, 0, 0, n - 1, val);
    }
};
