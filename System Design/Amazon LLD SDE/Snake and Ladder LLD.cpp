// Online C++ compiler to run C++ program online
#include <iostream>
#include<bits/stdc++.h>
using namespace std;

class Player{
    private: string playerName;
             int id;
    public: Player(string player,int id){
        this->playerName=player;
        this->id=id;
    }
    string getPlayerName(){
        return this->playerName;
    }
};

class Dice{
  private:int dices;
          
  public: Dice(int noOfDice){
      this->dices=noOfDice;
  }
  public: int rollDice(){
      //do the random number genertion from 1 to 6*noofDice.
      
      int randomNumber = (std::rand() % (6*dices)) + 1;
      return randomNumber;
  }  
};
class Snake{
    private: int startPoint;
             int endPoint;
    public: Snake(int startPoint,int endPoint){
            this->startPoint=startPoint;
            this->endPoint=endPoint;
    }
    public: int getStartPoint(){
        return this->startPoint;
    }
     public: int getEndPoint(){
        return this->endPoint;
    }
};
class Ladder{
    private: int startPoint;
             int endPoint;
    public: Ladder(int startPoint,int endPoint){
            this->startPoint=startPoint;
            this->endPoint=endPoint;
    }
    public: int getStartPoint(){
        return this->startPoint;
    }
     public: int getEndPoint(){
        return this->endPoint;
    }
};
class GameBoard{
    private: Dice* dice;
             queue<Player*>nextTurn;
             vector<Snake*>snakes;
             vector<Ladder*>ladders;
             unordered_map<string,int>playerCurrentPosition;
             int boardSize;
    public: GameBoard(Dice* dice,queue<Player*>nextTurn,vector<Snake*>snakes,
             vector<Ladder*>ladders,unordered_map<string,int>playerCurrentPosition,int boardSize){
        this->dice=dice;
        this->nextTurn=nextTurn;
        this->snakes=snakes;
        this->ladders=ladders;
        this->playerCurrentPosition=playerCurrentPosition;
        this->boardSize=boardSize;
    }
    //don't wait for last player to reach destination
    void startGame(){
        while(nextTurn.size()>1){
            Player* player=nextTurn.front();
            nextTurn.pop();
            
            int currPositionPlayer=playerCurrentPosition[player->getPlayerName()];
            int diceValue=dice->rollDice();
            int nextCell=currPositionPlayer+diceValue;
            if(nextCell>boardSize){
                nextTurn.push(player);
            }
            else if(nextCell==boardSize){
                cout<<player->getPlayerName() << "Won the Game"<<endl;
            }
            else{
                int nextPosi=nextCell;
                bool bitten=false;
                
                //iterate over ladders
                for(auto ladder:ladders){
                    if(ladder->getStartPoint()==nextCell){
                        nextPosi=ladder->getEndPoint();
                    }
                }
                if(nextPosi!=nextCell)cout<<player->getPlayerName() << "found ladder and the new position of the player is:" << nextPosi<<endl;
                
                //iterate over snakes
                for(auto snake:snakes){
                    if(snake->getStartPoint()==nextCell){
                        nextPosi=snake->getEndPoint();
                        bitten=true;
                    }
                }
                if(nextPosi!=nextCell && bitten)cout<<player->getPlayerName() << "was bitten and the new position of the player is:" << nextPosi<<endl;
                
                //update map if nextPosi in bounds
                if(nextPosi==boardSize){
                    cout<<player->getPlayerName() << "Won the Game"<<endl;
                }
                else{
                    //update
                    playerCurrentPosition[player->getPlayerName()]=nextPosi;
                    cout<<"Player"<< player->getPlayerName()+"is at position : "<<
                    nextPosi<<endl;
                    nextTurn.push(player);
                }
            }
            
        }
    }
};
int main() {
    // Write C++ code here
    srand(time(0));
    Dice* dice=new Dice(1);
    Player* p1=new Player("Pradyumn",1);
    Player* p2=new Player("Ayush",2);
    queue<Player*>players;
    players.push(p1);
    players.push(p2);
    Snake* snakeA=new Snake(30,5);
    Snake* snakeB=new Snake(20,10);
    Ladder* ladderA=new Ladder(40,92);
    Ladder* ladderB=new Ladder(24,44);
    vector<Snake*>snakes;
    snakes.push_back(snakeA);
    snakes.push_back(snakeB);
    vector<Ladder*>ladders;
    ladders.push_back(ladderA);
    ladders.push_back(ladderB);
    unordered_map<string,int>hash;
    hash[p1->getPlayerName()]=0;
    hash[p2->getPlayerName()]=0;
    GameBoard* game=new GameBoard(dice,players,snakes,ladders,hash,100);
    game->startGame();
    
    return 0;
}
