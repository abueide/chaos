//
// Created by Andrew on 7/24/2020.
//

#include <Windows.h>
#include "offsets.h"
#include <cstdio>
#include <iostream>
#include <vector>

using namespace std;

bool createConsole()
{
    if (!AllocConsole())
    {
        return false;
    }

    FILE* fDummy;
    freopen_s(&fDummy, "CONOUT$", "w", stdout);
    freopen_s(&fDummy, "CONOUT$", "w", stderr);
    freopen_s(&fDummy, "CONIN$", "r", stdin);
    std::cout.clear();
    std::clog.clear();
    std::cerr.clear();
    std::cin.clear();

    HANDLE hConOut = CreateFileA("CONOUT$", GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL,
                                 OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
    HANDLE hConIn = CreateFileA("CONIN$", GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL,
                                OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
    SetStdHandle(STD_OUTPUT_HANDLE, hConOut);
    SetStdHandle(STD_ERROR_HANDLE, hConOut);
    SetStdHandle(STD_INPUT_HANDLE, hConIn);
    std::wcout.clear();
    std::wclog.clear();
    std::wcerr.clear();
    std::wcin.clear();
    return true;
}

uintptr_t FindDMAAddy(uintptr_t ptr, std::vector<unsigned int> offsets) {
    uintptr_t addr = ptr;
    if (addr == NULL) {
        return NULL;
    }
    for (unsigned int i = 0; i < offsets.size(); ++i) {
        addr = *(uintptr_t *) addr;
        if (addr == NULL) {
            return NULL;
        }
        addr += offsets[i];
    }
    return addr;
}

uintptr_t FindLocalPlayer() {
    uintptr_t baseAddress = (uintptr_t) GetModuleHandle(TEXT("GameAssembly.dll"));
    uintptr_t local_player = FindDMAAddy(baseAddress + PLAYER_POINTER_OFFSET, PLAYER_OFFSETS);
    return local_player;
}

[[noreturn]] DWORD WINAPI LoopFunction(LPVOID lpParam) {

    while (true) {
        uintptr_t localPlayer = FindLocalPlayer();
        if (localPlayer != NULL) {
            int health = *(int *) (localPlayer + HEALTH); // health offset
            float x = *(float *) (localPlayer + POSITION_X);
            float y = *(float *) (localPlayer + POSITION_Y);
            printf("health=%d\n", health);
            printf("position=%f,%f\n", x, y);
            Sleep(200);
        }
        return 0;
    }
}

BOOL WINAPI DllMain(HINSTANCE hModule, DWORD dwAttached, LPVOID lpvReserved) {
    if (dwAttached == DLL_PROCESS_ATTACH) {
        createConsole();
        printf(GetCommandLine());
        printf("\n");

        CreateThread(NULL, 0, &LoopFunction, NULL, 0, NULL);
    }
    return 1;
}
