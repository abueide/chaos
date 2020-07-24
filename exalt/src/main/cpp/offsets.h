//
// Created by Andrew on 7/20/2020.
//

#include <vector>

#ifndef NULLHOOK_OFFSETS_H
#define NULLHOOK_OFFSETS_H


// pointers
const unsigned int PLAYER_POINTER_OFFSET = 0x037825A8;
const std::vector<unsigned int> PLAYER_OFFSETS = {0xD0, 0x160, 0x28, 0x0};

// Entity
// 4 byte int
const unsigned int MAX_HEALTH = 0x1C4;
const unsigned int HEALTH = 0x1C8;
const unsigned int MAX_MANA = 0x428;
// float
const unsigned int MANA = 0x42C;
const unsigned int POSITION_X = 0x10;
const unsigned int POSITION_Y = 0x10;

const unsigned int CONNECT = 0x000000018183C500;

class offsets {

};


#endif //NULLHOOK_OFFSETS_H
