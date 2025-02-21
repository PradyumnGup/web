// #ifndef TICTACTOEGAME_H
// #define TICTACTOEGAME_H
// #ifndef BOARD_H
// #define BOARD_H
// #ifndef PLAYER_H
// #define PLAYER_H

#include <vector>
#include <iostream>
#include <string>

using namespace std;

class Player {
private:
    string name;
    char symbol;

public:
    Player(string name, char symbol) : name(name), symbol(symbol) {}

    char getSymbol() const {
        return symbol;
    }

    string getName() const {
        return name;
    }
};





class Board {
private:
    int size;
    vector<vector<char>> grid;

public:
    Board(int n) : size(n) {
        grid = vector<vector<char>>(size, vector<char>(size, '.'));
    }

    void displayBoard() {
        for (const auto &row : grid) {
            for (char cell : row) {
                cout << cell << " ";
            }
            cout << "\n";
        }
        cout << "\n";
    }

    bool isValidMove(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size && grid[row][col] == '.';
    }

    void placeMove(int row, int col, char symbol) {
        grid[row][col] = symbol;
    }

    char getCell(int row, int col) const {
        return grid[row][col];
    }

    int getSize() const {
        return size;
    }
};




class TicTacToeGame {
private:
    Board board;
    vector<Player> players;
    int currentPlayerIndex;
    int movesPlayed;

public:
    TicTacToeGame(int boardSize, const vector<Player>& playerList)
        : board(boardSize), players(playerList), currentPlayerIndex(0), movesPlayed(0) {}

    void startGame() {
        while (movesPlayed < board.getSize() * board.getSize()) {
            board.displayBoard();
            Player currentPlayer = players[currentPlayerIndex];

            cout << "Player " << currentPlayer.getName() << " (" << currentPlayer.getSymbol() << ") - Enter row and column: ";
            int row, col;
            cin >> row >> col;

            if (!board.isValidMove(row, col)) {
                cout << "Invalid move! Try again.\n";
                continue;
            }

            board.placeMove(row, col, currentPlayer.getSymbol());
            movesPlayed++;

            if (checkWinner(row, col, currentPlayer.getSymbol())) {
                board.displayBoard();
                cout << "Player " << currentPlayer.getName() << " wins!\n";
                return;
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        board.displayBoard();
        cout << "It's a draw!\n";
    }

    bool checkWinner(int row, int col, char symbol) {
        return checkRow(row, symbol) || checkCol(col, symbol) || checkDiagonals(symbol);
    }

    bool checkRow(int row, char symbol) {
        for (int i = 0; i < board.getSize(); i++)
            if (board.getCell(row, i) != symbol) return false;
        return true;
    }

    bool checkCol(int col, char symbol) {
        for (int i = 0; i < board.getSize(); i++)
            if (board.getCell(i, col) != symbol) return false;
        return true;
    }

    bool checkDiagonals(char symbol) {
        bool mainDiag = true, antiDiag = true;
        for (int i = 0; i < board.getSize(); i++) {
            if (board.getCell(i, i) != symbol) mainDiag = false;
            if (board.getCell(i, board.getSize() - i - 1) != symbol) antiDiag = false;
        }
        return mainDiag || antiDiag;
    }
};




int main() {
    int size, numPlayers;
    cout << "Enter board size: ";
    cin >> size;
    cout << "Enter number of players: ";
    cin >> numPlayers;

    vector<Player> players;
    char symbol = 'X';
    for (int i = 0; i < numPlayers; i++) {
        string name;
        cout << "Enter player " << (i + 1) << " name: ";
        cin >> name;
        players.push_back(Player(name, symbol));

        if (symbol == 'X') symbol = 'O';  // First two players are X and O
        else symbol++;  // Other players get different symbols
    }

    TicTacToeGame game(size, players);
    game.startGame();

    return 0;
}
