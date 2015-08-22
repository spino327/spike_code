project(tiling)

cmake_minimum_required(VERSION 2.8.12)

set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -std=c++0x -O3 -DTAIL=${TAIL} -DN=${N}")
#set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -std=c++0x  -DTAIL=${TAIL}")
add_executable(mxv_tiling mxv_tiling.cpp)
