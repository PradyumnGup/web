#include <iostream>
using namespace std;

// Node structure for AVL Tree
struct Node {
    int key;
    Node* left;
    Node* right;
    int height;

    Node(int val) {
        key = val;
        left = right = nullptr;
        height = 1; // New node is initially at height 1
    }
};

// Get height of a node
int getHeight(Node* node) {
    return node ? node->height : 0;
}

// Get balance factor of a node
int getBalanceFactor(Node* node) {
    return node ? getHeight(node->left) - getHeight(node->right) : 0;
}

// Update height of a node
void updateHeight(Node* node) {
    if (node)
        node->height = 1 + max(getHeight(node->left), getHeight(node->right));
}

// Right rotation (LL Case)
Node* rightRotate(Node* y) {
    Node* x = y->left;
    Node* T2 = x->right;

    // Perform rotation
    x->right = y;
    y->left = T2;

    // Update heights
    updateHeight(y);
    updateHeight(x);

    return x; // New root
}

// Left rotation (RR Case)
Node* leftRotate(Node* x) {
    Node* y = x->right;
    Node* T2 = y->left;

    // Perform rotation
    y->left = x;
    x->right = T2;

    // Update heights
    updateHeight(x);
    updateHeight(y);

    return y; // New root
}

// Insert a key into AVL tree and return the new root
Node* insert(Node* node, int key) {
    if (!node) return new Node(key);

    // Perform standard BST insertion
    if (key < node->key)
        node->left = insert(node->left, key);
    else if (key > node->key)
        node->right = insert(node->right, key);
    else
        return node; // No duplicates allowed

    // Update height of current node
    updateHeight(node);

    // Get balance factor to check if it became unbalanced
    int balance = getBalanceFactor(node);

    // **Perform Rotations If Necessary**
    // Left-Left (LL) Case
    if (balance > 1 && key < node->left->key)
        return rightRotate(node);

    // Right-Right (RR) Case
    if (balance < -1 && key > node->right->key)
        return leftRotate(node);

    // Left-Right (LR) Case
    if (balance > 1 && key > node->left->key) {
        node->left = leftRotate(node->left);
        return rightRotate(node);
    }

    // Right-Left (RL) Case
    if (balance < -1 && key < node->right->key) {
        node->right = rightRotate(node->right);
        return leftRotate(node);
    }

    return node; // Return unchanged node pointer if balanced
}

// Preorder traversal to print the tree
void preOrder(Node* root) {
    if (root) {
        cout << root->key << " ";
        preOrder(root->left);
        preOrder(root->right);
    }
}

// Driver Code
int main() {
    Node* root = nullptr;
    int keys[] = {10, 20, 30, 40, 50, 25};

    for (int key : keys)
        root = insert(root, key);

    cout << "Preorder traversal of AVL tree: ";
    preOrder(root);
    cout << endl;

    return 0;
}
