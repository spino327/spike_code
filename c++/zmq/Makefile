CC=g++
CFLAGS=-c -pedantic -std=c++11 -g 
LDFLAGS=-lzmq
SOURCES=hwserver.cpp hwclient.cpp wuserver.cpp wuclient.cpp
OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=hwserver hwclient wuserver wuclient

#target: dependencies
#[tab] system command

all: $(SOURCES) $(EXECUTABLE)
	
$(EXECUTABLE): $(OBJECTS)
	$(CC) $(@).o -o $@ $(LDFLAGS)

.cpp.o:
	$(CC) $(CFLAGS) $< -o $@ -I$(HOME)/usr/include/cppzmq

clean:
	rm *.o $(EXECUTABLE)
