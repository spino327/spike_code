/*
 * wuclient.cpp
 *
 *  Created on: Apr 13, 2015
 *      Author: pinogal
 */

#include <zmq.hpp>
#include <ctime>
#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <sstream>

using namespace std;

string USAGE = "USAGE: ./app num_updates <filter>";

int main (int argc, char *argv[]) {

    if (argc < 2) {
        cerr << USAGE << "\n";
        exit(-1);
    }

    zmq::context_t context(1);

    // socket to talk to server
    cout << "Collecting updates from weather server...\n\n";
    zmq::socket_t subscriber (context, ZMQ_SUB);
//    subscriber.connect("tcp://localhost:5556");
    subscriber.connect("ipc://weather.ipc");

    int total_updates = atoi(argv[1]);

    const char* filter = (argc > 2 ? argv[2] : "10001 ");
    subscriber.setsockopt(ZMQ_SUBSCRIBE, filter, strlen(filter));

    // Process
    int update_nbr;
    long total_temp = 0;
    for (update_nbr = 0; update_nbr < total_updates; ++update_nbr) {
        zmq::message_t update;
        int zipcode, temperature, relhumidity;

        subscriber.recv(&update);

        istringstream iss (static_cast<char*> (update.data()));
        iss >> zipcode >> temperature >> relhumidity;

        total_temp += temperature;
    }
    cout << "Average temperature for zipcode '" << filter
            << "' was " << (int) (total_temp / update_nbr) << "F\n";

    return 0;
}
