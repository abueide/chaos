//
// Created by Andrew on 7/25/2020.
//
#include <cstdio>
#include <string>
#include <rpc.h>

using namespace std;

class ILSTR
{
public:
    ILSTR() {}
    ILSTR(std::wstring m_wstrString)
    {
        m_pSetString(m_wstrString);
    }

    void m_pSetString(std::wstring m_wstrString)
    {
        unsigned int i = 0;
        for (; i < m_wstrString.size(); i++)
        {
            string[i] = m_wstrString.c_str()[i];
        }
        string[++i] = (wchar_t)L"";
        length = m_wstrString.length();
    }

    ILSTR& operator=(std::wstring m_wstrString)
    {
        m_pSetString(m_wstrString);
        return *this;
    }

    unsigned int m_nLength()
    {
        return length;
    }

    std::wstring m_wstrString()
    {
        return string;
    }



    const wchar_t * m_pGetString()
    {
        return (const wchar_t*)(DWORD64(this) + 0x14);
    }
private:

    char pad0x0[16];
    unsigned int length;
    wchar_t string[50] = {};
};
typedef ILSTR UnityEngineString;
