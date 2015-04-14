CC=g++
CFLAGS=-c -pedantic -Wc++11-extensions -std=c++11 -g 
LDFLAGS=-lzmq
SOURCES=hwserver.cpp hwclient.cpp wuserver.cpp wuclient.cpp
OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=hwserver hwclient wuserver wuclient

#target: dependencies
#[tab] system command

all: $(SOURCES) $(EXECUTABLE)
	
$(EXECUTABLE): $(OBJECTS)
	$(CC) $(LDFLAGS) $(@).o -o $@

.cpp.o:
	$(CC) $(CFLAGS) $< -o $@ -I/Users/pinogal/usr/include/cppzmq

clean:
	rm *.o $(EXECUTABLE)