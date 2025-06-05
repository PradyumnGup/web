class Solution {
public:
    void dfs(string airport,vector<string>&answer,unordered_map<string,vector<string>>&graph,vector<vector<string>>& tickets){
        auto& destinations = graph[airport];
        while (!destinations.empty()) {
            string next = destinations.back();
            destinations.pop_back();
            dfs(next,answer,graph,tickets);
        }
        answer.push_back(airport); 
        return ;
    }
    vector<string> findItinerary(vector<vector<string>>& tickets) {
        vector<string>answer;

        unordered_map<string,vector<string>>graph;
        for(auto ticket:tickets){
            graph[ticket[0]].push_back(ticket[1]);
            
        }
         for (auto& [from, toList] : graph) {
            sort(toList.rbegin(), toList.rend());
        }
        dfs("JFK",answer,graph,tickets);
        reverse(answer.begin(), answer.end());
        return answer;
    }
};
