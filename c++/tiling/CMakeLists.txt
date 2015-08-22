project(tiling)

cmake_minimum_required(VERSION 2.8.12)

set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -std=c++0x -O3 -DTILE=${TILE} -DN=${N}")
#set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -std=c++0x  -DTILE=${TILE}")
add_executable(mxv_tiling mxv_tiling.cpp)
