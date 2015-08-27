project(tiling)

cmake_minimum_required(VERSION 2.8.12)

set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -std=c++0x -O3")
if (${N})
    set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -DN=${N}")
endif ()
if (${TILE})
    set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -DTILE=${TILE}")
endif ()

add_executable(mxv_tiling mxv_tiling.cpp)
