//
// Created by ashcon on 8/10/20.
//

#ifndef MERCEDES_UI_READBUFFER_H
#define MERCEDES_UI_READBUFFER_H
#include "stdlib.h"
#include <mutex>
#define RLOG_TAG "ReadBuffer"

class readBuffer {
public:
    /**
     * Creates a read buffer to store any data coming from the Arduino
     * @param maxCapacity Maximum capacity of the buffer in bytes
     */
    readBuffer(int maxCapacity);
    /**
     * Destructor
     */
    ~readBuffer();

    /**
     * Inserts 1 element to the back of the buffer
     * @param elm Element to insert
     */
    void insertElement(uint8_t elm);

    /**
     * Inserts an array of elements to the buffer
     * @param ptr Pointer to the array
     * @param numElements Number of elements to add from pointer
     */
    void insertElements(uint8_t* ptr, int numElements);

    /**
     * Gets the current number of elements being stored in the buffer
     * @return Current number of elements being stored
     */
    int getSize() const;

    /**
     * Reads from the buffer until a character
     * @param ptr Pointer to array to write to
     * @param maxBytes Max number of character to read if target cannot be found
     * @param until Target end character to read until
     * @return Actual number of bytes read
     */
    int readUntil(uint8_t* ptr, int maxBytes, uint8_t until);
private:
    std::mutex mutex; // Mutex
    uint8_t* buffer; // Read buffer
    int storedElements; // Number of stored elements
    int maxElements; // Limit size
    void shiftBuffer(int amount);
};
#endif //MERCEDES_UI_READBUFFER_H
