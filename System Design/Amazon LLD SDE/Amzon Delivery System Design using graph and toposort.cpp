#include <iostream>
#include <vector>
#include <queue>
#include <unordered_map>
#include <unordered_set>

using namespace std;

class Parcel {
public:
    int id;
    Parcel(int id) : id(id) {}
};

class DeliveryStation {
public:
    int id;
    vector<int> outgoingStations; // Stations where parcels can be sent
    DeliveryStation(int id) : id(id) {}
};

class DeliveryNetwork {
public:
    unordered_map<int, DeliveryStation*> stations;
    unordered_map<int, int> inDegree; // Track in-degrees for Kahn’s algorithm

    void addStation(int id) {
        if (stations.find(id) == stations.end()) {
            stations[id] = new DeliveryStation(id);
        }
    }

    void addRoute(int from, int to) {
        addStation(from);
        addStation(to);
        stations[from]->outgoingStations.push_back(to);
        inDegree[to]++;
    }

    vector<int> topologicalSort() {
        vector<int> sortedStations;
        queue<int> q;

        // Push stations with no incoming routes
        for (auto &entry : stations) {
            if (inDegree[entry.first] == 0) {
                q.push(entry.first);
            }
        }

        while (!q.empty()) {
            int station = q.front();
            q.pop();
            sortedStations.push_back(station);

            for (int neighbor : stations[station]->outgoingStations) {
                if (--inDegree[neighbor] == 0) {
                    q.push(neighbor);
                }
            }
        }

        if (sortedStations.size() != stations.size()) {
            throw runtime_error("Cycle detected! Cannot perform topological sort.");
        }

        return sortedStations;
    }
};

class Request {
public:
    int parcelId;
    vector<pair<int, int>> routes; // Pairs of (from, to) station ids

    Request(int parcelId, vector<pair<int, int>> routes) : parcelId(parcelId), routes(routes) {}
};

class Response {
public:
    bool success;
    string message;

    Response(bool success, string message) : success(success), message(message) {}
};

class DeliveryService {
public:
    DeliveryNetwork network;

    Response processRequest(Request request) {
        for (auto route : request.routes) {
            network.addRoute(route.first, route.second);
        }

        try {
            vector<int> sortedStations = network.topologicalSort();
            return Response(true, "Successfully processed. Stations order: " + to_string(sortedStations.size()));
        } catch (runtime_error &e) {
            return Response(false, e.what());
        }
    }
};

int main() {
    DeliveryService service;

    Request request(1, {{1, 2}, {2, 3}, {3, 4}});
    Response response = service.processRequest(request);

    cout << (response.success ? "Success: " : "Failure: ") << response.message << endl;
    return 0;
}
