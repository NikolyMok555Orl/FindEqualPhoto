@echo off
"C:\\Nikoly\\Program\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Nikoly\\MyProgranAndPet\\Android\\workTest\\FindEqualPhoto\\FindEqualPhoto\\openCV460\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Nikoly\\Program\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_ANDROID_NDK=C:\\Nikoly\\Program\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Nikoly\\Program\\Android\\Sdk\\ndk\\23.1.7779620\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Nikoly\\Program\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Nikoly\\MyProgranAndPet\\Android\\workTest\\FindEqualPhoto\\FindEqualPhoto\\openCV460\\build\\intermediates\\cxx\\Debug\\6q44v3f3\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Nikoly\\MyProgranAndPet\\Android\\workTest\\FindEqualPhoto\\FindEqualPhoto\\openCV460\\build\\intermediates\\cxx\\Debug\\6q44v3f3\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Nikoly\\MyProgranAndPet\\Android\\workTest\\FindEqualPhoto\\FindEqualPhoto\\openCV460\\.cxx\\Debug\\6q44v3f3\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"