#include <iostream>
#include <queue>
#include <unordered_map>
#include <vector>
using namespace std;

class Parcel {
public:
    int id;
    string destination;

    Parcel(int id, string destination) : id(id), destination(destination) {}
};

class Station {
public:
    string name;
    queue<Parcel> parcels;

    Station(string name) : name(name) {}

    void receiveParcel(const Parcel& parcel) {
        parcels.push(parcel);
    }

    Parcel dispatchParcel() {
        if (parcels.empty()) {
            throw runtime_error("No parcels to dispatch.");
        }
        Parcel parcel = parcels.front();
        parcels.pop();
        return parcel;
    }

    bool hasParcels() const {
        return !parcels.empty();
    }
};

class DeliveryNetwork {
private:
    unordered_map<string, Station> stations;

public:
    void addStation(string name) {
        stations[name] = Station(name);
    }

    void sendParcel(int id, string from, string to) {
        if (stations.find(from) == stations.end() || stations.find(to) == stations.end()) {
            throw runtime_error("Invalid station(s).");
        }
        stations[from].receiveParcel(Parcel(id, to));
    }

    void processParcels() {
        for (auto& [name, station] : stations) {
            if (station.hasParcels()) {
                Parcel parcel = station.dispatchParcel();
                if (stations.find(parcel.destination) != stations.end()) {
                    stations[parcel.destination].receiveParcel(parcel);
                    cout << "Parcel " << parcel.id << " moved from " << name << " to " << parcel.destination << "\n";
                } else {
                    cout << "Parcel " << parcel.id << " lost: Invalid destination " << parcel.destination << "\n";
                }
            }
        }
    }
};

int main() {
    DeliveryNetwork network;
    network.addStation("A");
    network.addStation("B");
    network.addStation("C");

    network.sendParcel(1, "A", "B");
    network.sendParcel(2, "B", "C");
    network.sendParcel(3, "C", "A");

    network.processParcels();
    return 0;
}
// Potential Improvements

//     Introduce dependency injection to make DeliveryService more flexible.
//     Implement polymorphism in stations to support different types (e.g., SortingCenter, Warehouse).
//     Use smart pointers instead of raw pointers in DeliveryNetwork for better memory management.
