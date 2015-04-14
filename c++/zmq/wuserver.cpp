/*
 * wuserver.cpp
 *
 *  Created on: Apr 13, 2015
 *      Author: pinogal
 */

#include <zmq.hpp>
#include <ctime>
#include <cstdlib>
#include <cstdio>

#define within(num) (int) ((float) num * random () / (RAND_MAX + 1.0))

int main () {

    // context
    zmq::context_t context (1);
    zmq::socket_t publisher (context, ZMQ_PUB);
    publisher.bind("tcp://*:5556");
    publisher.bind("ipc://weather.ipc");

    // initialize random number generator
    srandom ((unsigned) time (nullptr));
    while (true) {
        int zipcode, temperature, relhumidity;

        // get values
        zipcode = 10001;
        temperature = within (215) - 80;
        relhumidity = within (50) + 10;

        // Send message to all subscribers
        zmq::message_t message (20);
        snprintf ((char*) message.data(), 20, "%05d %d %d", zipcode, temperature, relhumidity);
        publisher.send(message);
    }
    return 0;
}
