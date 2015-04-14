/*
 * hwserver.cpp
 *
 * Hellow wrold server
 *
 *  Created on: Apr 13, 2015
 *      Author: pinogal
 */

#include <zmq.hpp>
#include <string>
#include <iostream>
#include <unistd.h>

using namespace std;

int main () {

    int major, minor, patch;
    zmq::version(&major, &minor, &patch);
    cout << "Current 0MQ version is " << major << "." << minor << "." << patch << "\n";

    // context
    zmq::context_t context (1);

    // socket
    zmq::socket_t socket (context, ZMQ_REP);
    socket.bind("tcp://*:5555");

    while (true) {
        // wait for next request from client
        zmq::message_t request;

        socket.recv(&request);
        cout << "Received Hello" << endl;

        // do some 'work'
        sleep (1);

        // send reply back to client
        zmq::message_t reply (5);
        std::memcpy (reply.data(), "World", 5);
        socket.send (reply);
    }
    return 0;
}
