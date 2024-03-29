# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.20

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/ashcon/code/MercedesUI/app

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/ashcon/code/MercedesUI/app/cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/canbus-lib.dir/depend.make
# Include the progress variables for this target.
include CMakeFiles/canbus-lib.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/canbus-lib.dir/flags.make

CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.o: CMakeFiles/canbus-lib.dir/flags.make
CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.o: ../src/main/cpp/canbus.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/ashcon/code/MercedesUI/app/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.o -c /home/ashcon/code/MercedesUI/app/src/main/cpp/canbus.cpp

CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/ashcon/code/MercedesUI/app/src/main/cpp/canbus.cpp > CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.i

CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/ashcon/code/MercedesUI/app/src/main/cpp/canbus.cpp -o CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.s

CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.o: CMakeFiles/canbus-lib.dir/flags.make
CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.o: ../src/main/cpp/readBuffer.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/ashcon/code/MercedesUI/app/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.o -c /home/ashcon/code/MercedesUI/app/src/main/cpp/readBuffer.cpp

CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/ashcon/code/MercedesUI/app/src/main/cpp/readBuffer.cpp > CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.i

CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/ashcon/code/MercedesUI/app/src/main/cpp/readBuffer.cpp -o CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.s

CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.o: CMakeFiles/canbus-lib.dir/flags.make
CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.o: ../src/main/cpp/CanbusDecoder.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/ashcon/code/MercedesUI/app/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.o -c /home/ashcon/code/MercedesUI/app/src/main/cpp/CanbusDecoder.cpp

CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/ashcon/code/MercedesUI/app/src/main/cpp/CanbusDecoder.cpp > CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.i

CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/ashcon/code/MercedesUI/app/src/main/cpp/CanbusDecoder.cpp -o CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.s

CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.o: CMakeFiles/canbus-lib.dir/flags.make
CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.o: ../src/main/cpp/AGW.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/ashcon/code/MercedesUI/app/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building CXX object CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.o -c /home/ashcon/code/MercedesUI/app/src/main/cpp/AGW.cpp

CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/ashcon/code/MercedesUI/app/src/main/cpp/AGW.cpp > CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.i

CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/ashcon/code/MercedesUI/app/src/main/cpp/AGW.cpp -o CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.s

CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.o: CMakeFiles/canbus-lib.dir/flags.make
CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.o: ../src/main/cpp/AGWDisplay.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/ashcon/code/MercedesUI/app/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Building CXX object CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.o -c /home/ashcon/code/MercedesUI/app/src/main/cpp/AGWDisplay.cpp

CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/ashcon/code/MercedesUI/app/src/main/cpp/AGWDisplay.cpp > CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.i

CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/ashcon/code/MercedesUI/app/src/main/cpp/AGWDisplay.cpp -o CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.s

# Object files for target canbus-lib
canbus__lib_OBJECTS = \
"CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.o" \
"CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.o" \
"CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.o" \
"CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.o" \
"CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.o"

# External object files for target canbus-lib
canbus__lib_EXTERNAL_OBJECTS =

libcanbus-lib.so: CMakeFiles/canbus-lib.dir/src/main/cpp/canbus.cpp.o
libcanbus-lib.so: CMakeFiles/canbus-lib.dir/src/main/cpp/readBuffer.cpp.o
libcanbus-lib.so: CMakeFiles/canbus-lib.dir/src/main/cpp/CanbusDecoder.cpp.o
libcanbus-lib.so: CMakeFiles/canbus-lib.dir/src/main/cpp/AGW.cpp.o
libcanbus-lib.so: CMakeFiles/canbus-lib.dir/src/main/cpp/AGWDisplay.cpp.o
libcanbus-lib.so: CMakeFiles/canbus-lib.dir/build.make
libcanbus-lib.so: CMakeFiles/canbus-lib.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/ashcon/code/MercedesUI/app/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_6) "Linking CXX shared library libcanbus-lib.so"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/canbus-lib.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/canbus-lib.dir/build: libcanbus-lib.so
.PHONY : CMakeFiles/canbus-lib.dir/build

CMakeFiles/canbus-lib.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/canbus-lib.dir/cmake_clean.cmake
.PHONY : CMakeFiles/canbus-lib.dir/clean

CMakeFiles/canbus-lib.dir/depend:
	cd /home/ashcon/code/MercedesUI/app/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/ashcon/code/MercedesUI/app /home/ashcon/code/MercedesUI/app /home/ashcon/code/MercedesUI/app/cmake-build-debug /home/ashcon/code/MercedesUI/app/cmake-build-debug /home/ashcon/code/MercedesUI/app/cmake-build-debug/CMakeFiles/canbus-lib.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/canbus-lib.dir/depend

