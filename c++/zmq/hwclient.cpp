/*
 * hwclient.cpp
 *
 *  Created on: Apr 13, 2015
 *      Author: pinogal
 */

#include <zmq.hpp>
#include <string>
#include <iostream>

using namespace std;

int main () {
    // Prepare our context and socket
    zmq::context_t context (1);
    zmq::socket_t socket (context, ZMQ_REQ);

    cout << "Connecting to hello world server..." << endl;
    socket.connect("tcp://localhost:5555");

    for (int request_nbr = 0; request_nbr < 10; ++request_nbr) {
        zmq::message_t request (5);
        memcpy (request.data(), "Hello", 5);
        cout << "Sending Hello " << request_nbr << "...\n";
        socket.send(request);

        // Get the reply
        zmq::message_t reply;
        socket.recv(&reply);
        cout << "Received World " << request_nbr << endl;
    }
    return 0;
}
