#ifndef RGB_H_
#define RGB_H_

#include <Adafruit_TLC5947.h>
#define TLC5974_DATA  2
#define TLC5947_CLOCK 3
#define TLC5947_LATCH 6


// Emulating the default Yellow lighting found in the W203
#define DEFAULT_R 246 << 4
#define DEFAULT_G 190 << 4
#define DEFAULT_B 0 << 4

class RGB_Channel {
public:
    RGB_Channel(uint8_t idx);
    // Called every 10ms
    void update(Adafruit_TLC5947* tlc);
    void set_colour(uint8_t step_count, uint8_t r, uint8_t g, uint8_t b);
private:
    uint8_t idx;

    float c_r;
    float c_g;
    float c_b;

    int steps_left = 0;
    float d_r; // Per step
    float d_g; // Per step
    float d_b; // Per step
};


class RGB_Manager {
public:
    RGB_Manager();
    void write_channel(uint8_t channel_id, uint8_t step_count, uint8_t r, uint8_t g, uint8_t b);
    // Called every 10ms
    void update();
private:
    Adafruit_TLC5947 *tlc;
    RGB_Channel* channels[8];
};

#endif