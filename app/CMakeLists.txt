cmake_minimum_required(VERSION 3.4.1)

file(GLOB app_src_files
       "src/main/cpp/*.cpp" )

add_library( native-lib SHARED ${app_src_files} )

include_directories(SYSTEM ${CMAKE_SOURCE_DIR}/src/main/include/node )

add_library( nodejs-lib SHARED IMPORTED)

set_target_properties( nodejs-lib
                      PROPERTIES IMPORTED_LOCATION
                      ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libnode.so )

find_library( log-lib log )
target_link_libraries( native-lib ${log-lib} nodejs-lib )
