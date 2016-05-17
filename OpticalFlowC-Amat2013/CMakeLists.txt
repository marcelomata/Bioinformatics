#CMake file to build Tracking_GMM project


# refer to the root source directory of the project as ${TGMM_SOURCE_DIR} and
# to the root binary directory of the project as ${TGMM_BINARY_DIR}. 
#The FindCUDA script is distributed since version 2.8

cmake_minimum_required (VERSION 2.8)
project (opticalFlowECCV)



#
#Set important flags
#

# Call  "cmake-gui -DCMAKE_BUILD_TYPE=Release .." on the command line to specify a particular build, take their definition
# and dump it in the cache along with proper documentation, otherwise set CMAKE_BUILD_TYPE
# to Debug prior to calling PROJECT()
#
IF( CMAKE_BUILD_TYPE STREQUAL "")
	SET(CMAKE_BUILD_TYPE Debug CACHE STRING "Choose the type of build, options are: None(CMAKE_CXX_FLAGS or CMAKE_C_FLAGS used) Debug Release RelWithDebInfo MinSizeRel." FORCE)
ELSE() #Debug is the default
	SET(CMAKE_BUILD_TYPE ${CMAKE_BUILD_TYPE} CACHE STRING "Choose the type of build, options are: None(CMAKE_CXX_FLAGS or CMAKE_C_FLAGS used) Debug Release RelWithDebInfo MinSizeRel.")
ENDIF()

MESSAGE("CMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE}")

#to locate scripts
set (CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/cmake") 


#
# Find necessary libraries
#

#mylib (it will comiple it on-the-fly)
set(MYLIB_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/mylib)
add_subdirectory(${MYLIB_ROOT})
set(MYLIB_INCLUDE_DIR ${MYLIB_ROOT})
include_directories(${MYLIB_INCLUDE_DIR})

#it automatically adds for each configuration
SET(MYLIB_LIBRARIES_DIR ${CMAKE_CURRENT_BINARY_DIR}/mylib ${CMAKE_CURRENT_BINARY_DIR}/mylib/MY_TIFF)
SET(MYLIB_LIBRARIES mylib mytiff)
LINK_DIRECTORIES(${MYLIB_LIBRARIES_DIR})
LINK_DIRECTORIES(${CMAKE_CURRENT_BINARY_DIR})

#
# includes
#

#
# submodules: add source and header files from subfolders
#

file(GLOB OpticalFlow_SRCS opticalFlow/*.cpp)
file(GLOB OpticalFlow_HDRS opticalFlow/*.h)

file(GLOB_RECURSE ExternalC_SRCS external/*.c)
file(GLOB_RECURSE ExternalCXX_SRCS external/*.cpp)
file(GLOB_RECURSE External_HDRS external/*.h)




add_executable(opticalFlow mainOpticalFlow.cpp ${OpticalFlow_SRCS} ${OpticalFlow_HDRS} ${ExternalC_SRCS} ${ExternalCXX_SRCS} ${External_HDRS})



#
# linked libraries
#

IF(WIN32)
set_target_properties(opticalFlow PROPERTIES COMPILE_FLAGS "-W2")
ELSE()
set_target_properties(opticalFlow PROPERTIES COMPILE_FLAGS "-Wall")
ENDIF()

TARGET_LINK_LIBRARIES( opticalFlow ${MYLIB_LIBRARIES})


#
# Source groups for IDE
#

#We need each external folder separately
file(GLOB External_lbfgs_SRCS external/lbfgs/*.cpp)
file(GLOB External_Nathan_SRCS external/Nathan/*.c)
file(GLOB External_xmlParser_SRCS external/xmlParser2/*.cpp)

file(GLOB External_Mylib_HDRS mylib/*.h)

file(GLOB External_lbfgs_HDRS external/lbfgs/*.h)
file(GLOB External_Nathan_HDRS external/Nathan/*.h)
file(GLOB External_xmlParser_HDRS external/xmlParser2/*.h)


source_group("Header Files"  FILES ${MAIN_CLASSES_HDRS})
source_group("Header Files\\opticalFlow"  FILES ${OpticalFlow_HDRS})
source_group("Header Files\\External\\lbfgs" FILES ${External_lbfgs_HDRS})
source_group("Header Files\\External\\Nathan" FILES ${External_Nathan_HDRS})
source_group("Header Files\\External\\xmlParser" FILES ${External_xmlParses_HDRS})
source_group("Header Files\\mylib" FILES ${External_Mylib_HDRS})

source_group("Source Files" FILES mainOpticalFlow.cpp)
source_group("Source Files\\opticalFlow"  FILES ${OpticalFlow_SRCS})
source_group("Source Files\\External\\lbfgs" FILES ${External_lbfgs_SRCS})
source_group("Source Files\\External\\Nathan" FILES ${External_Nathan_SRCS})
source_group("Source Files\\External\\xmlParser" FILES ${External_xmlParses_SRCS})
