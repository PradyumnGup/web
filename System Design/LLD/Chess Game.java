
import java.util.*;


 enum Status {
	    ACTIVE, SAVED, BLACK_WIN, WHITE_WIN, STALEMATE;
}
interface BoardGames {
  // This interface illustrates how a large game company can manage multiple
  // types of games, including board games and non-board games. Tic Tac Toe is
  // an example of a game that is a child of the BoardGames interface.
}

class Player{
    private String name;
    private boolean isWhitePlayer;

    public Player(String name,boolean isWhitePlayer){
        this.name=name;
        this.isWhitePlayer=isWhitePlayer;
    }
    public String getName(){
        return name;
    }
    public boolean getPlayerColor(){
        return isWhitePlayer;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setPlayerColor(boolean isWhitePlayer){
        this.isWhitePlayer=isWhitePlayer;
    }
}

// Common Interface for the Move Strategies
 interface MovementStrategy {
  boolean canMove(Board board, Cell startCell, Cell endCell);
}
class KingMovementStrategy implements MovementStrategy {

    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {

        int rowDiff = Math.abs(startCell.getRow() - endCell.getRow());
        int colDiff = Math.abs(startCell.getCol() - endCell.getCol());

        // King can move only one square in any direction
        return rowDiff <= 1 && colDiff <= 1
                && (rowDiff + colDiff > 0);
    }
}
class QueenMovementStrategy implements MovementStrategy {

    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {

        int rowDiff = Math.abs(startCell.getRow() - endCell.getRow());
        int colDiff = Math.abs(startCell.getCol() - endCell.getCol());

        // Queen moves horizontally, vertically or diagonally
        boolean isStraightMove = rowDiff == 0 || colDiff == 0;
        boolean isDiagonalMove = rowDiff == colDiff;

        if (!isStraightMove && !isDiagonalMove) {
            return false;
        }

        return isPathClear(board, startCell, endCell);
    }

    private boolean isPathClear(Board board, Cell startCell, Cell endCell) {

        int rowStep = Integer.compare(
                endCell.getRow(), startCell.getRow());

        int colStep = Integer.compare(
                endCell.getCol(), startCell.getCol());

        int row = startCell.getRow() + rowStep;
        int col = startCell.getCol() + colStep;

        while (row != endCell.getRow() || col != endCell.getCol()) {

            if (board.getCell(row, col).getPiece() != null) {
                return false;
            }

            row += rowStep;
            col += colStep;
        }

        return true;
    }
}
class BishopMovementStrategy implements MovementStrategy {

    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {

        int rowDiff = Math.abs(startCell.getRow() - endCell.getRow());
        int colDiff = Math.abs(startCell.getCol() - endCell.getCol());

        // Bishop can move only diagonally
        if (rowDiff != colDiff || rowDiff == 0) {
            return false;
        }

        return isPathClear(board, startCell, endCell);
    }

    private boolean isPathClear(Board board, Cell startCell, Cell endCell) {

        int rowStep = Integer.compare(
                endCell.getRow(), startCell.getRow());

        int colStep = Integer.compare(
                endCell.getCol(), startCell.getCol());

        int row = startCell.getRow() + rowStep;
        int col = startCell.getCol() + colStep;

        while (row != endCell.getRow() || col != endCell.getCol()) {

            if (board.getCell(row, col).getPiece() != null) {
                return false;
            }

            row += rowStep;
            col += colStep;
        }

        return true;
    }
}
class KnightMovementStrategy implements MovementStrategy {

    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {

        int rowDiff = Math.abs(startCell.getRow() - endCell.getRow());
        int colDiff = Math.abs(startCell.getCol() - endCell.getCol());

        return (rowDiff == 2 && colDiff == 1)
                || (rowDiff == 1 && colDiff == 2);
    }
}
class RookMovementStrategy implements MovementStrategy {

    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {

        int rowDiff = Math.abs(startCell.getRow() - endCell.getRow());
        int colDiff = Math.abs(startCell.getCol() - endCell.getCol());

        // Rook moves horizontally or vertically
        if (rowDiff != 0 && colDiff != 0) {
            return false;
        }

        // Same source and destination
        if (rowDiff == 0 && colDiff == 0) {
            return false;
        }

        return isPathClear(board, startCell, endCell);
    }

    private boolean isPathClear(Board board, Cell startCell, Cell endCell) {

        int rowStep = Integer.compare(endCell.getRow(), startCell.getRow());
        int colStep = Integer.compare(endCell.getCol(), startCell.getCol());

        int row = startCell.getRow() + rowStep;
        int col = startCell.getCol() + colStep;

        while (row != endCell.getRow() || col != endCell.getCol()) {

            if (board.getCell(row, col).getPiece() != null) {
                return false;
            }

            row += rowStep;
            col += colStep;
        }

        return true;
    }
}

class PawnMovementStrategy implements MovementStrategy {

    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {

        Piece pawn = startCell.getPiece();

        int direction = pawn.isWhite() ? 1 : -1;

        int rowDiff = endCell.getRow() - startCell.getRow();
        int colDiff = endCell.getCol() - startCell.getCol();

        // Case 1: Move one square forward
        if (colDiff == 0 && rowDiff == direction) {
            return endCell.getPiece() == null;
        }

        // Case 2: Move two squares forward from starting position
        int startingRow = pawn.isWhite() ? 1 : 6;

        if (colDiff == 0
                && rowDiff == 2 * direction
                && startCell.getRow() == startingRow) {

            int intermediateRow = startCell.getRow() + direction;

            return board.getCell(intermediateRow, startCell.getCol())
                    .getPiece() == null
                    && endCell.getPiece() == null;
        }

        // Case 3: Capture diagonally
        if (Math.abs(colDiff) == 1 && rowDiff == direction) {

            Piece destinationPiece = endCell.getPiece();

            return destinationPiece != null
                    && destinationPiece.isWhite() != pawn.isWhite();
        }

        return false;
    }
}

abstract class Piece{
    private boolean isWhitePiece;
    private boolean killed = false; // is the piece killed or not 
    private MovementStrategy movementStrategy;
     // Constrcutor  
    public Piece(boolean isWhitePiece,MovementStrategy movementStrategy) { 
        this.isWhitePiece = isWhitePiece; 
        this.movementStrategy = movementStrategy;
    } 
    // returns piece type 
    public boolean isWhite() { 
        return isWhitePiece; 
    } 
    // returns whether a piece has been killed or not 
    public boolean isKilled() { 
        return killed; 
    } 
    // modifes the piece status upon being killed 
    public void setKilled(boolean killed) { 
        this.killed = killed; 
    } 

    public boolean canMove(Board board, Cell startBlock, Cell endBlock) {
        return movementStrategy.canMove(board, startBlock, endBlock);
    }
}
class King extends Piece{
    public King(boolean isWhitePiece){
        super(isWhitePiece,new KingMovementStrategy());
    }
    @Override
  public boolean canMove(Board board, Cell startCell, Cell endCell) {
    return super.canMove(board, startCell, endCell);
  }
}
 class Queen extends Piece {
    public Queen(boolean isWhitePiece) {
        super(isWhitePiece,new QueenMovementStrategy());
    }
     @Override
  public boolean canMove(Board board, Cell startCell, Cell endCell) {
    return super.canMove(board, startCell, endCell);
  }
}

class Bishop extends Piece {
    public Bishop(boolean isWhitePiece){
        super(isWhitePiece,new BishopMovementStrategy());
    }
    @Override
  public boolean canMove(Board board, Cell startCell, Cell endCell) {
    return super.canMove(board, startCell, endCell);
  }
}

class Knight extends Piece {
    public Knight(boolean isWhitePiece) {
        super(isWhitePiece, new KnightMovementStrategy());
    }
   }
class Rook extends Piece {

    public Rook(boolean isWhitePiece) {
        super(isWhitePiece, new RookMovementStrategy());
    }
}

class Pawn extends Piece {

    public Pawn(boolean isWhitePiece) {
        super(isWhitePiece, new PawnMovementStrategy());
    }
}

class PieceFactory{
    public static Piece createPiece(String pieceType, boolean isWhitePiece) {
            switch (pieceType.toLowerCase()) {
          case "king":
            return new King(isWhitePiece);
          case "queen":
            return new Queen(isWhitePiece);
          case "bishop":
            return new Bishop(isWhitePiece);
          case "knight":
            return new Knight(isWhitePiece);
          case "rook":
            return new Rook(isWhitePiece);
          case "pawn":
            return new Pawn(isWhitePiece);
          default:
            throw new IllegalArgumentException("Unknown piece type: " + pieceType);
        }
    }
}

class Cell{
    private int row,col;
    private String label;
    private Piece piece;

    // constructor
  public Cell(int row, int col, Piece piece) {
    this.row = row;
    this.col = col;
    this.piece = piece;
  }
  // eturns the current piece on the cell
  public Piece getPiece() {
    return piece;
  }
  // puts a piece on the cell
  public void setPiece(Piece piece) {
    this.piece = piece;
  }
    public int getRow() {
    return row;
}

public int getCol() {
    return col;
}
}

class Move {
  private Cell startCell;
  private Cell endCell;
  // Constructor to initialize the move with start and end cells
  public Move(Cell startCell, Cell endCell) {
    this.startCell = startCell;
    this.endCell = endCell;
  }
  // Validate if the move is valid
  public boolean isValid() {

    Piece source = startCell.getPiece();
    Piece destination = endCell.getPiece();

    if (source == null) {
        return false;
    }

    if (destination == null) {
        return true;
    }

    return source.isWhite() != destination.isWhite();
}
  // gets the start cell
  public Cell getStartCell() {
    return startCell;
  }
  // gets the end cell
  public Cell getEndCell() {
    return endCell;
  }
}
class Board{
    private Cell[][]board;
    private static Board instance;
    
    private Board(int rows){
        initializeBoard(rows);
    }
    public static Board getInstance(int rows){
        if(instance==null){
            instance=new Board(rows);
        }
        return instance;
    }
    // Initialize the board
    private void initializeBoard(int rows) {
        board = new Cell[rows][rows];
        // Setting White Pieces using PieceFactory
    setPieceRow(0, true);
    setPawnRow(1, rows, true);
    // Setting Black Pieces using PieceFactory
    setPieceRow(rows - 1, false);
    setPawnRow(rows - 2, rows, false);
    // Defining rest of the cells having no pieces
    for (int row = 2; row < rows - 2; row++) {
      for (int col = 0; col < rows; col++) {
        board[row][col] = new Cell(row, col, null);
      }
    }
}

      // Set the major pieces for a given row
  private void setPieceRow(int row, boolean isWhite) {
      board[row][0] = new Cell(row, 0, PieceFactory.createPiece("rook", isWhite));
    board[row][1] =
        new Cell(row, 1, PieceFactory.createPiece("knight", isWhite));
    board[row][2] =
        new Cell(row, 2, PieceFactory.createPiece("bishop", isWhite));
    board[row][3] =
        new Cell(row, 3, PieceFactory.createPiece("queen", isWhite));
    board[row][4] = new Cell(row, 4, PieceFactory.createPiece("king", isWhite));
    board[row][5] =
        new Cell(row, 5, PieceFactory.createPiece("bishop", isWhite));
    board[row][6] =
        new Cell(row, 6, PieceFactory.createPiece("knight", isWhite));
    board[row][7] = new Cell(row, 7, PieceFactory.createPiece("rook", isWhite));
  }
    // Set pawns for a given row
  private void setPawnRow(int row, int rows, boolean isWhite) {
      for (int j = 0; j < rows; j++) {
          board[row][j] = new Cell(row, j, PieceFactory.createPiece("pawn", isWhite));
      }
  }
    public Cell getCell(int row, int col) {
    return board[row][col];
}
}



class ChessGame implements BoardGames {
    private Board board;
    private Player player1;
    private Player player2;
    private boolean isWhiteTurn;
    private ArrayList<Move> gameLog;
    private Status status;

    public ChessGame(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = Board.getInstance(8);
        this.isWhiteTurn = true;
        this.status = Status.ACTIVE;
        this.gameLog = new ArrayList<>();
    }

    // Expose the board for testing/display
    public Board getBoard() {
        return board;
    }

    public void makeMove(Move move, Player player) {

        if (status != Status.ACTIVE) {
            return;
        }

        // Validate player turn
        if (isWhiteTurn != player.getPlayerColor()) {
            return;
        }

        // Validate source cell
        if (move.getStartCell().getPiece() == null) {
            return;
        }

        Piece sourcePiece = move.getStartCell().getPiece();

        // Validate ownership
        if (sourcePiece.isWhite() != player.getPlayerColor()) {
            return;
        }

        // Validate destination and piece movement
        if (!move.isValid()) {
            return;
        }

        if (!sourcePiece.canMove(
                board, move.getStartCell(), move.getEndCell())) {
            return;
        }

        Piece destinationPiece = move.getEndCell().getPiece();

        // Capture destination piece
        if (destinationPiece != null) {
            if (destinationPiece instanceof King) {
                status = isWhiteTurn
                        ? Status.WHITE_WIN
                        : Status.BLACK_WIN;
                return;
            }

            destinationPiece.setKilled(true);
        }

        // Execute move
        gameLog.add(move);

        move.getEndCell().setPiece(sourcePiece);
        move.getStartCell().setPiece(null);

        // Switch turn
        isWhiteTurn = !isWhiteTurn;
    }

    public Status getStatus() {
        return status;
    }
}




class Main {
    public static void main(String[] args) {
        // Create players
        Player player1 = new Player("Player1", true);
        Player player2 = new Player("Player2", false);

        // Initialize game
        ChessGame chessGame = new ChessGame(player1, player2);

        // Get board from ChessGame
        Board board = chessGame.getBoard();

        // White moves pawn from (1,0) to (2,0)
        Cell whiteStart = board.getCell(1, 0);
        Cell whiteEnd = board.getCell(2, 0);

        Move move1 = new Move(whiteStart, whiteEnd);
        chessGame.makeMove(move1, player1);

        // Black moves pawn from (6,0) to (5,0)
        Cell blackStart = board.getCell(6, 0);
        Cell blackEnd = board.getCell(5, 0);

        Move move2 = new Move(blackStart, blackEnd);
        chessGame.makeMove(move2, player2);

        // Verify moves
        System.out.println(
            "White pawn at (2,0): "
            + (board.getCell(2, 0).getPiece() != null)
        );

        System.out.println(
            "Black pawn at (5,0): "
            + (board.getCell(5, 0).getPiece() != null)
        );
    }
}

//o/p
// White pawn at (2,0): true
// Black pawn at (5,0): true
