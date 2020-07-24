#include <Windows.h>
#include "offsets.h"
#include <cstdio>
#include <iostream>
#include "minhook/MinHook.h"
#include <psapi.h>
#pragma comment(lib, "psapi.lib")
#define INRANGE(x,a,b)    (x >= a && x <= b)
#define getBits( x )    (INRANGE((x&(~0x20)),'A','F') ? ((x&(~0x20)) - 'A' + 0xa) : (INRANGE(x,'0','9') ? x - '0' : 0))
#define getByte( x )    (getBits(x[0]) << 4 | getBits(x[1]))

using namespace std;

bool m_bCreateConsole()
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

DWORD64 FindPatternIDA64(std::string moduleName, std::string pattern)
{
    const char* pat = pattern.c_str();
    DWORD64 m_dwFirstMatch = 0;
    DWORD64 m_dwModule = (DWORD64)GetModuleHandleA(moduleName.c_str());
    MODULEINFO m_miModuleInformation;
    K32GetModuleInformation(GetCurrentProcess(), (HMODULE)m_dwModule, &m_miModuleInformation, sizeof(MODULEINFO));

    for (DWORD64 pCur = m_dwModule; pCur < (m_dwModule + m_miModuleInformation.SizeOfImage); pCur++)
    {
        if (!*pat)
            return m_dwFirstMatch;

        if (*(PBYTE)pat == '\?' || *(BYTE*)pCur == getByte(pat))
        {
            if (!m_dwFirstMatch)
                m_dwFirstMatch = pCur;

            if (!pat[2])
                return m_dwFirstMatch;

            (*(PWORD)pat == '\?\?' || *(PBYTE)pat != '\?') ? pat += 3 : pat += 2;
        }
        else
        {
            pat = pattern.c_str();
            m_dwFirstMatch = 0;
        }
    }
    return NULL;
}


void* m_pLocalPlayerPointer = nullptr;
void* m_pCacheEntityOriginal = nullptr;

void* __fastcall m_pCacheEntityDetour(void* ECX, void* EDX)
{
    if (ECX) //check if the ecx/entity/this is valid/not a nullptr
    {
        if (*(bool*)(std::uintptr_t(ECX) + 0x3A)) //check if the entity is local player (0x3A = m_nIsLocalPlayerOffset)
        {
            m_pLocalPlayerPointer = ECX; //if its the localplayer, make the localplayer pointer point to it
        }
    }

    return ((void*(__thiscall*)(void*))(m_pCacheEntityOriginal))(ECX); //remember to call the games original function
}

INPUT m_pConstructKeyBoardInput(WORD m_wVirtualKey, DWORD m_dwFlags)
{
    INPUT m_inpInput;
    m_inpInput.type = INPUT_KEYBOARD;
    m_inpInput.ki.wVk = m_wVirtualKey;
    m_inpInput.ki.dwFlags = m_dwFlags;
    m_inpInput.ki.wScan = MapVirtualKeyExA(m_wVirtualKey, 0, GetKeyboardLayout(GetCurrentThreadId()));
    m_inpInput.ki.time = 0;
    m_inpInput.ki.dwExtraInfo = 0;

    return m_inpInput;
}

void m_pForceKeyDown(DWORD m_dwKey)
{
    INPUT input = m_pConstructKeyBoardInput(m_dwKey, KEYEVENTF_SCANCODE);
    SendInput(1, &input, sizeof(INPUT));
}

void m_pForceKeyUp(DWORD m_dwKey)
{
    INPUT input = m_pConstructKeyBoardInput(m_dwKey, KEYEVENTF_KEYUP);
    SendInput(1, &input, sizeof(INPUT));
}

void m_pHoldKeyDown(DWORD m_dwKey, DWORD m_dwTimeMs = 1)
{
    m_pForceKeyDown(m_dwKey);
    Sleep(m_dwTimeMs);
    m_pForceKeyUp(m_dwKey);
}


int m_nAutonexusValue = 20; //0 - 100
//set default autonexus value to 20
char m_strNexusKey = 'R'; //set the nexus key



void m_pCheckHealthThread()
{
    MH_Initialize();
    m_bCreateConsole();
    void* m_pCacheEntityFunction = (void*)FindPatternIDA64(
        "GameAssembly.dll", "48 89 7C 24 18 55 48 8B EC 48 81 EC 80 ? ? ? 80 3D ? 8D 33 02");
    //find the cache entity function with a pattern
    if (MH_CreateHook(m_pCacheEntityFunction, &m_pCacheEntityDetour,
        reinterpret_cast<LPVOID*>(&m_pCacheEntityOriginal)) != MH_OK)
    {
        printf("failed to detour\n");
    }
    if (MH_EnableHook(m_pCacheEntityFunction) != MH_OK)
    {
        printf("failed to enable hook\n");
    }

    while (true) //make the thread run forever/the programs lifetime
    {
        if (m_pLocalPlayerPointer) //check if our localplayer pointer is valid/not a nullptr
        {
            int m_nMaxHealth = *(int*)(std::uintptr_t(m_pLocalPlayerPointer) + 0x1C4);
            //get local player max health (0x1C4 = m_nMaxHealthOffset)
            int m_nHealth = *(int*)(std::uintptr_t(m_pLocalPlayerPointer) + 0x1C8);
            //get local player health (0x1C8 = m_nHealthOffset)
            int m_nHealthPercentage = 100.f * (float(m_nHealth) / float(m_nMaxHealth)); //0 - 100

            if (m_nHealthPercentage < m_nAutonexusValue) //we have less health % than the minimum allowed, nexus
            {
                m_pHoldKeyDown(m_strNexusKey, 20);
            }
        }

        Sleep(5); //check every 5 ms, any less will probably put too much of a strain on cpu for something so simple
    }
}




BOOL WINAPI DLLMain(HINSTANCE hinstDLL, DWORD dwReason, LPVOID lpReserved) //The DLL Main Entry
{
    switch (dwReason)
    {
    case DLL_PROCESS_ATTACH:
        //detour it to make the game call our function instead of its own
        CreateThread(NULL, NULL, (LPTHREAD_START_ROUTINE)m_pCheckHealthThread, NULL, NULL, NULL);
        //start the thread that is to check our health
        break;
    case DLL_PROCESS_DETACH:
    case DLL_THREAD_ATTACH:
    case DLL_THREAD_DETACH:
        break;
    }
    return TRUE;
}
