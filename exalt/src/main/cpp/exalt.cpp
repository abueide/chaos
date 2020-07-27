/*#include "windows.h"
#include "offsets.h"
#include <cstdio>
#include <iostream>
#include "MinHook.h"
#include <psapi.h>

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
                printf("MANUAL AUTONEXUS ALERT");
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
}*/

#include <Windows.h>
#include "offsets.h"
#include <cstdio>
#include <iostream>
#include <Psapi.h>
#include "MinHook.h"
#include "ILSTR.cpp"

#define INRANGE(x, a, b)    (x >= a && x <= b)
#define getBits(x)    (INRANGE((x&(~0x20)),'A','F') ? ((x&(~0x20)) - 'A' + 0xa) : (INRANGE(x,'0','9') ? x - '0' : 0))
#define getByte(x)    (getBits(x[0]) << 4 | getBits(x[1]))

using namespace std;

bool createConsole() {
    if (!AllocConsole()) {
        return false;
    }

    FILE *fDummy;
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

DWORD64 FindPatternIDA64(std::string moduleName, std::string pattern, int count) {
    const char *pat = pattern.c_str();
    DWORD64 m_dwFirstMatch = 0;
    DWORD64 m_dwModule = (DWORD64) GetModuleHandleA(moduleName.c_str());
    MODULEINFO m_miModuleInformation;
    K32GetModuleInformation(GetCurrentProcess(), (HMODULE) m_dwModule, &m_miModuleInformation, sizeof(MODULEINFO));

    int matchCounter = 0;
    for (DWORD64 pCur = m_dwModule; pCur < (m_dwModule + m_miModuleInformation.SizeOfImage); pCur++) {
        if (!*pat) {
            if(++matchCounter == count) {
                return m_dwFirstMatch;
            }
        }

        if (*(PBYTE) pat == '\?' || *(BYTE *) pCur == getByte(pat)) {
            if (!m_dwFirstMatch)
                m_dwFirstMatch = pCur;

            if (!pat[2]) {
                if(++matchCounter == count) {
                    return m_dwFirstMatch;
                }
            }

            (*(PWORD) pat == '\?\?' || *(PBYTE) pat != '\?') ? pat += 3 : pat += 2;
        } else {
            pat = pattern.c_str();
            m_dwFirstMatch = 0;
        }
    }
    return NULL;
}

void *m_pLocalPlayerPointer = nullptr;
void *m_pCacheEntityOriginal = nullptr;

void *__fastcall m_pCacheEntityDetour(void *ECX) {
    if (ECX) //check if the ecx/entity/this is valid/not a nullptr
    {
        if (*(bool *) (std::uintptr_t(ECX) + 0x3A)) //check if the entity is local player (0x3A = m_nIsLocalPlayerOffset)
        {
            m_pLocalPlayerPointer = ECX; //if its the localplayer, make the localplayer pointer point to it
        }
    }

    return ((void *(__thiscall *)(void *)) (m_pCacheEntityOriginal))(ECX); //remember to call the games original function
}

bool detourEntityCache(){
    void *m_pCacheEntityFunction = (void *) FindPatternIDA64(
            "GameAssembly.dll", "48 89 7C 24 18 55 48 8B EC 48 81 EC 80 ? ? ? 80 3D ? 8D 33 02", 1);
    //find the cache entity function with a pattern
    if (MH_CreateHook(m_pCacheEntityFunction, &m_pCacheEntityDetour,
                      reinterpret_cast<LPVOID *>(&m_pCacheEntityOriginal)) != MH_OK) {
        printf("failed to detour\n");
        return false;
    }
    if (MH_EnableHook(m_pCacheEntityFunction) != MH_OK) {
        printf("failed to enable hook\n");
        return false;
    }
    return true;
}

void *connectOriginal = nullptr;
void * _socketmanager = nullptr;
ILSTR* _ip = nullptr;
int port = 2050;
ILSTR* _in = nullptr;
ILSTR* _out = nullptr;
bool foundConnect = false;

typedef uintptr_t*(__thiscall * Connect)(void*, ILSTR*, int, ILSTR*, ILSTR*);

void *__stdcall connectDetour(void* socketmanager, ILSTR* ip, int port, ILSTR* incomingCipher, ILSTR* outgoingCipher){
    wprintf(L"%ls\n", ip->m_pGetString());
    _socketmanager = socketmanager;
    _ip = ip;
    _in = incomingCipher;
    _out = outgoingCipher;
    foundConnect = true;
    return ((Connect)(connectOriginal))(socketmanager, ip, port, incomingCipher, outgoingCipher);
}

bool detourConnect(){
    uintptr_t connectAddress = FindPatternIDA64("GameAssembly.dll", "E8 ? ? ? ? 48 8B 0B 48 85 C9 0F 84 ? ? ? ? 48 8B 49 10", 2);
    uintptr_t correctAddress = reinterpret_cast<uintptr_t>(GetModuleHandle("GameAssembly.dll")) + 0x183B400;
//    printf("%llX\n", connectAddress);
//    printf("%llX\n", correctAddress);
    void *connectFunction = (void *) correctAddress;
    if (MH_CreateHook(connectFunction, connectDetour, reinterpret_cast<LPVOID*>(&connectOriginal)) != MH_OK){
        printf("Failed to detour %llX\n", connectAddress);
        return false;
    }
    if (MH_EnableHook(connectFunction) != MH_OK) {
        printf("failed to enable hook\n");
        return false;
    }
    return true;
}

typedef uintptr_t*(__thiscall * Nexus)(void* param1, void* param2);

void *nexusOriginal = nullptr;
void *_param1 = nullptr;
void *_param2 = nullptr;
bool foundNexus = false;

void *__stdcall nexusDetour(void* param1, void* param2){
    _param1 = param1;
    _param2 = param2;
//    wprintf(L"%ls\n", param1->m_pGetString());
//    wprintf(L"%ls\n", param2->m_pGetString());
//    wprintf(L"%ls\n", param3->m_pGetString());
    foundNexus = true;
    uintptr_t nexusAddress = reinterpret_cast<uintptr_t>(GetModuleHandle("GameAssembly.dll")) + 0x12CA590;
//    Nexus nexus = (Nexus) nexusAddress;
    return ((Nexus) nexusOriginal)(param1, param2);
}

bool detourNexus(){
    uintptr_t nexusAddress = reinterpret_cast<uintptr_t>(GetModuleHandle("GameAssembly.dll")) + 0x12CA590;
    void *nexusFunction = (void *) nexusAddress;
    if (MH_CreateHook(nexusFunction, nexusDetour, reinterpret_cast<LPVOID*>(&nexusOriginal)) != MH_OK){
        printf("Failed to detour %llX\n", nexusAddress);
        return false;
    }
    if (MH_EnableHook(nexusFunction) != MH_OK) {
        printf("failed to enable hook\n");
        return false;
    }
    return true;
}

int m_nAutonexusValue = 80; //0 - 100

[[noreturn]] DWORD WINAPI LoopFunction(LPVOID lpParam) {
    detourConnect();
    detourEntityCache();
    detourNexus();
    // GameAssembly.dll + 0x0cf4b50
    Nexus nexus = (Nexus)(reinterpret_cast<uintptr_t>(GetModuleHandle("GameAssembly.dll")) + 0x12CA590);
    int counter = 0;
    while (true) {
        if (m_pLocalPlayerPointer) //check if our localplayer pointer is valid/not a nullptr
        {
            int m_nMaxHealth = *(int *) (std::uintptr_t(m_pLocalPlayerPointer) + 0x1C4);
            //get local player max health (0x1C4 = m_nMaxHealthOffset)
            int m_nHealth = *(int *) (std::uintptr_t(m_pLocalPlayerPointer) + 0x1C8);
            //get local player health (0x1C8 = m_nHealthOffset)
            int m_nHealthPercentage = 100.f * (float(m_nHealth) / float(m_nMaxHealth)); //0 - 100

            if (m_nHealthPercentage < m_nAutonexusValue) //we have less health % than the minimum allowed, nexus
            {
                if(foundConnect) {
                    printf("%llX", _socketmanager);
                    wprintf(L"%ls\n", _ip->m_pGetString());
                    wprintf(L"%ls\n", _in->m_pGetString());
                    wprintf(L"%ls\n", _out->m_pGetString());
                }
                if(_socketmanager != nullptr && _in != nullptr && _out != nullptr && _ip != nullptr) {
                    uintptr_t correctAddress = reinterpret_cast<uintptr_t>(GetModuleHandle("GameAssembly.dll")) + 0x183B400;
                    Connect connect = (Connect) correctAddress;
                    connect(_socketmanager, _ip, port, _in, _out);
                uintptr_t nexusAddress = reinterpret_cast<uintptr_t>(GetModuleHandle("GameAssembly.dll")) + 0x12CA590;
            }
        }
        Sleep(20);
    }
}

BOOL WINAPI DllMain(HINSTANCE hModule, DWORD dwAttached, LPVOID lpvReserved) {
    if (dwAttached == DLL_PROCESS_ATTACH) {
        createConsole();
        MH_Initialize();
        CreateThread(NULL, 0, &LoopFunction, NULL, 0, NULL);
    }
    return 1;
}
